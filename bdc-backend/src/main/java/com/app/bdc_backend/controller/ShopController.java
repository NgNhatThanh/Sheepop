package com.app.bdc_backend.controller;

import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.address.Ward;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.request.AddProductDTO;
import com.app.bdc_backend.model.dto.response.*;
import com.app.bdc_backend.model.enums.PaymentStatus;
import com.app.bdc_backend.model.enums.PaymentType;
import com.app.bdc_backend.model.enums.ShopOrderStatus;
import com.app.bdc_backend.model.order.OrderItem;
import com.app.bdc_backend.model.order.ShopOrder;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.shop.ShopAddress;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.*;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopController {

    private final UserService userService;

    private final ShopService shopService;

    private final FollowService followService;

    private final ProductService productService;

    private final ReviewService reviewService;

    private final AddressService addressService;

    private final OrderService orderService;

    @GetMapping("/info/{username}")
    public ResponseEntity<?> getShopInfo(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if(user == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "User not found!"
            ));
        }
        Shop shop = shopService.findByUser(user);
        if(shop == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "User doesn't have a shop"
            ));
        }
        return ResponseEntity.ok(toShopResponseDTO(shop));
    }

    @GetMapping("/base/{shopId}")
    public ResponseEntity<?> getShopBase(@PathVariable String shopId) {
        Shop shop = shopService.findById(shopId);
        if(shop == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Shop not found!"
            ));
        }
        return ResponseEntity.ok(toShopResponseDTO(shop));
    }

    @PostMapping("/product/add")
    public ResponseEntity<?> addProduct(@RequestBody AddProductDTO productDTO) {
        try{
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByUsername(username);
            Shop shop = shopService.findByUser(user);
            Product product = ModelMapper.getInstance().map(productDTO, Product.class);
            product.setShop(shop);
            product.setCreatedAt(new Date());
            Category category = ModelMapper.getInstance().map(productDTO.getCategory(), Category.class);
            product.setCategory(category);

            for(ProductSKU sku : product.getSkuList()){
                sku.setProduct(product);
                productService.addProductAttributeList(sku.getAttributes());
            }
            productService.addProductMediaList(product.getMediaList());
            productService.addProductSKUList(product.getSkuList());
            productService.saveProduct(product);
            return ResponseEntity.ok().build();
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/product/list")
    public ResponseEntity<PageResponse<ShopProductTableResponseDTO>> getProductList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                                    @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                                                    @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                                    @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        Sort sort = Sort.by(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<Product> productList = productService.findByShop(shop, pageable);
        PageResponse<ShopProductTableResponseDTO> response = new PageResponse<>(
                productList.map(this::toProductTableDTO)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/get_list")
    public ResponseEntity<?> getShopOrderList(@RequestParam(value = "type") int type,
                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam (value = "limit")int limit){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        Pageable pageable = PageRequest.of(page, limit);
        Page<ShopOrder> data = null;
        switch (type){
            case 0:
                data = orderService.getShopOrderByShop(shop, pageable);
                break;
            case 1:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.PENDING),
                        pageable);
                break;
            case 2:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.PREPARING),
                        pageable);
                break;
            case 3:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.SENT),
                        pageable);
                break;
            case 4:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.DELIVERED, ShopOrderStatus.COMPLETED, ShopOrderStatus.RATED),
                        pageable);
                break;
            case 5:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.CANCELLED),
                        pageable);
                break;
        }
        if(data == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request"
            ));
        }
        Page<ShopOrderDTO> dtos = data.map(this::toShopOrderDTO);
        PageResponse<ShopOrderDTO> response = new PageResponse<>(dtos);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/order/update")
    public ResponseEntity<?> updateOrderStatus(@RequestParam(value = "shopOrderId") String shopOrderId,
                                               @RequestParam(value = "currentStatus") int currentStatus){
        if(currentStatus > 2){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid request"
            ));
        }
        ShopOrder shopOrder = orderService.getShopOrderById(shopOrderId);
        if(shopOrder == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid shop order id"
            ));
        }
        if(shopOrder.getStatus() != currentStatus){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid current status"
            ));
        }
        if(currentStatus == ShopOrderStatus.PENDING) shopOrder.setStatus(ShopOrderStatus.PREPARING);
        else shopOrder.setStatus(ShopOrderStatus.SENT);
        orderService.saveAllShopOrders(List.of(shopOrder));
        return ResponseEntity.ok().build();
    }

    private ShopOrderDTO toShopOrderDTO(ShopOrder shopOrder) {
        ShopOrderDTO dtoShopOrder = new ShopOrderDTO();
        dtoShopOrder.setId(shopOrder.getId().toString());
        dtoShopOrder.setUsername(shopOrder.getShop().getUser().getUsername());
        dtoShopOrder.setName(shopOrder.getShop().getName());
        dtoShopOrder.setCompletedPayment(shopOrder.getOrder().getPayment().getStatus() != PaymentStatus.PENDING
                    || shopOrder.getOrder().getPayment().getType() == PaymentType.COD);
        dtoShopOrder.setStatus(shopOrder.getStatus());
        dtoShopOrder.setCreatedAt(shopOrder.getCreatedAt());
        dtoShopOrder.setShippingFee(shopOrder.getShippingFee());
        List<OrderItemDTO> itemDtos = new ArrayList<>();
        for(OrderItem item : shopOrder.getItems()){
            OrderItemDTO dtoItem = new OrderItemDTO();
            dtoItem.setId(item.getId().toString());
            dtoItem.setQuantity(item.getQuantity());
            dtoItem.setPrice(item.getPrice());
            dtoItem.setAttributes(item.getAttributes());
            dtoItem.getProduct().setId(item.getProduct().getId().toString());
            dtoItem.getProduct().setName(item.getProduct().getName());
            dtoItem.getProduct().setThumbnailUrl(item.getProduct().getThumbnailUrl());
            itemDtos.add(dtoItem);
        }
        dtoShopOrder.setItems(itemDtos);
        return dtoShopOrder;
    }


    private ShopProductTableResponseDTO toProductTableDTO(Product product) {
        ShopProductTableResponseDTO dto = new ShopProductTableResponseDTO();
        dto.setId(product.getId().toString());
        dto.setName(product.getName());
        dto.setThumbnailUrl(product.getThumbnailUrl());
        dto.setVisible(product.isVisible());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        if(!product.getSkuList().isEmpty()){
            for(ProductSKU sku : product.getSkuList()){
                dto.setQuantity(dto.getQuantity() + sku.getQuantity());
                dto.getSkuList().add(ModelMapper.getInstance().map(sku, ProductSKUResponseDTO.class));
            }
        }
        else{
            dto.setQuantity(product.getQuantity());
            dto.setPrice(product.getPrice());
        }
        return dto;
    }

    @PostMapping("/address/save")
    public ResponseEntity<?> saveAddress(@RequestBody AddAddressDTO addressDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        ShopAddress address = shopService.findAddressByShopId(shop.getId().toString());
        if(address == null) address = fromDTOtoAddress(addressDTO);
        else{
            ShopAddress tmp = fromDTOtoAddress(addressDTO);
            address.setProvince(tmp.getProvince());
            address.setDistrict(tmp.getDistrict());
            address.setWard(tmp.getWard());
            address.setDetail(tmp.getDetail());
        }
        address.setShopId(shop.getId().toString());
        shopService.saveAddress(address);
        return ResponseEntity.ok(address);
    }

    private ShopAddress fromDTOtoAddress(AddAddressDTO dto){
        ShopAddress address = new ShopAddress();
        address.setDetail(dto.getDetail());
        Province province = addressService.findProvinceByName(dto.getProvince());
        if(province == null){
            throw new RuntimeException("Địa chỉ không hợp lệ");
        }
        address.setProvince(province);
        List<District> districts = addressService.findDistrictByName(dto.getDistrict());
        boolean okDistrict = false;
        for(District d : districts){
            if(d.getProvinceId() == province.getId()){
                okDistrict = true;
                address.setDistrict(d);
                break;
            }
        }
        if(!okDistrict){
            throw new RuntimeException("Địa chỉ không hợp lệ");
        }
        List<Ward> ward = addressService.findWardByName(dto.getWard());
        boolean okWard = false;
        for(Ward w : ward){
            if(w.getDistrictId() == address.getDistrict().getId()){
                okWard = true;
                address.setWard(w);
                break;
            }
        }
        if(!okWard){
            throw new RuntimeException("Địa chỉ không hợp lệ");
        }
        return address;
    }

    private ShopResponseDTO toShopResponseDTO(Shop shop) {
        int followerCount = followService.getShopFollowCount(shop.getId().toString());
        int productCount = productService.countProductOfShop(shop.getId().toString());
        Map<String, String> ratingInfo = reviewService.getCountAndAverageReviewOfShop(shop.getId().toString());
        ShopResponseDTO dto = ModelMapper.getInstance().map(shop, ShopResponseDTO.class);
        dto.setShopId(shop.getId().toString());
        dto.setFollowerCount(followerCount);
        dto.setProductCount(productCount);
        dto.setReviewCount(Integer.parseInt(ratingInfo.get("reviewCount")));
        dto.setAverageRating(Float.parseFloat(ratingInfo.get("averageRating")));
        return dto;
    }

}
