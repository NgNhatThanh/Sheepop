package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.address.Ward;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.request.SaveProductDTO;
import com.app.bdc_backend.model.dto.request.UpdateShopProfileDTO;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ShopFacadeService {

    private final UserService userService;

    private final ShopService shopService;

    private final AddressService addressService;

    private final ProductService productService;

    private final OrderService orderService;

    private final CategoryService categoryService;

    private final ProductFacadeService productFacadeService;

    public ShopProfileDTO getShopProfile(String username) {
        User user = userService.findByUsername(username);
        if(user == null)
            throw new RequestException("User not found");
        Shop shop = shopService.findByUser(user);
        if(!shop.isActive())
            throw new RequestException("Shop is not active");
        if(shop.isDeleted())
            throw new RequestException("Shop was banned");
        return toShopProfileDTO(shop);
    }

    public ShopProfileDTO updateShopProfile(UpdateShopProfileDTO dto) {
        if(dto.getShopName() == null || dto.getShopName().isEmpty())
            throw new RequestException("Shop name is empty");
        if(dto.getId() == null || dto.getId().isEmpty())
            throw new RequestException("Shop id is empty");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        if(!shop.getId().toString().equals(dto.getId()))
            throw new RequestException("Invalid request: unknown shop owner");
        shop.setName(dto.getShopName());
        shop.setDescription(dto.getDescription());
        shop.setAvatarUrl(dto.getAvatarUrl());
        shopService.save(shop);
        return toShopProfileDTO(shop);
    }

    public ShopAddress getShopAddress(String username) {
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        if(!shop.isActive())
            throw new RequestException("Shop is not active");
        if(shop.isDeleted())
            throw new RequestException("Shop was banned");
        return shopService.findAddressByShopId(shop.getId().toString());
    }

    public ProductResponseDTO previewProduct(String productId) {
        Product product = productService.findById(productId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!product.getShop().getUser().getUsername().equals(username)) {
            throw new RequestException("Invalid request: not your shop's product");
        }
        return productFacadeService.getProduct(productId, true);
    }

    public Product getProductForEdit(String productId){
        return productService.findById(productId);
    }

    public ShopAddress setAddress(AddAddressDTO addressDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        ShopAddress address = shopService.findAddressByShopId(shop.getId().toString());
        if(address == null) address = fromDTOtoAddress(addressDTO);
        else{
            ShopAddress tmp = fromDTOtoAddress(addressDTO);
            address.setSenderName(tmp.getSenderName());
            address.setPhoneNumber(tmp.getPhoneNumber());
            address.setProvince(tmp.getProvince());
            address.setDistrict(tmp.getDistrict());
            address.setWard(tmp.getWard());
            address.setDetail(tmp.getDetail());
        }
        address.setShopId(shop.getId().toString());
        shopService.saveAddress(address);
        return address;
    }

    public void deleteProduct(String productId) {
        Product product = productService.findById(productId);
        if(product == null)
            throw new RequestException("Invalid reques: Product not found");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!product.getShop().getUser().getUsername().equals(username)) {
            throw new RequestException("Invalid request: not your shop's product");
        }
        productService.delete(product);
    }

    public Page<ShopOrderDTO> getShopOrders(int type,
                                            int filterType,
                                            String keyword,
                                            int page,
                                            int limit){
        if(filterType < 0 || filterType > 3){
            throw new RequestException("Invalid request: filter type");
        }
        if(filterType >= 1 && (keyword == null || keyword.isEmpty())){
            throw new RequestException("Invalid request: filter data");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        Pageable pageable = PageRequest.of(page, limit);
        Page<ShopOrder> data = null;
        switch (type){
            case 0:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        ShopOrderStatus.getAllStatuses(),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 1:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.PENDING),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 2:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.PREPARING),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 3:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.SENT, ShopOrderStatus.DELIVERING),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 4:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.COMPLETED, ShopOrderStatus.RATED),
                        pageable,
                        filterType,
                        keyword);
                break;
            case 5:
                data = orderService.getShopOrderByShopAndStatus(shop,
                        List.of(ShopOrderStatus.CANCELLED),
                        pageable,
                        filterType,
                        keyword);
                break;
        }
        if(data == null){
            throw new RequestException("Invalid request");
        }
        return data.map(this::toShopOrderDTO);
    }

    public void updateOrder(String shopOrderId,
                            int currentStatus) {
        if(currentStatus > 2){
            throw new RequestException("Invalid request: status");
        }
        ShopOrder shopOrder = orderService.getShopOrderById(shopOrderId);
        if(shopOrder == null){
            throw new RequestException("Invalid shop order id");
        }
        if(shopOrder.getStatus() != currentStatus){
            throw new RequestException( "Invalid current status");
        }
        if(currentStatus == ShopOrderStatus.PENDING){
            shopOrder.setStatus(ShopOrderStatus.PREPARING);
        }
        else{
            shopOrder.setStatus(ShopOrderStatus.DELIVERING);
        }
        orderService.saveAllShopOrders(List.of(shopOrder));
    }

    public void addProduct(SaveProductDTO productDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        ShopAddress address = shopService.findAddressByShopId(shop.getId().toString());
        if(address == null)
            throw new RequestException("Invalid request: shop hasn't have address yet");
        Product product = ModelMapper.getInstance().map(productDTO, Product.class);
        product.setShop(shop);
        product.setCreatedAt(new Date());
        Category category = ModelMapper.getInstance().map(productDTO.getCategory(), Category.class);
        product.setCategory(category);
        for(ProductSKU sku : product.getSkuList()){
            sku.setProduct(product);
            productService.addProductAttributeList(sku.getAttributes());
        }
        categoryService.increaseProductCount(category);
        productService.addProductMediaList(product.getMediaList());
        productService.addProductSKUList(product.getSkuList());
        productService.saveProduct(product);
    }

    public void updateProduct(SaveProductDTO dto) {
        Product product = productService.findById(dto.getProductId());
        if(product == null){
            throw new RequestException("Invalid request: product not found");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!product.getShop().getUser().getUsername().equals(username)){
            throw new RequestException("Invalid request: not your shop's product");
        }
        Product updatedProd = ModelMapper.getInstance().map(dto, Product.class);
        updatedProd.setId(product.getId());
        updatedProd.setUpdatedAt(new Date());
        updatedProd.setCreatedAt(product.getCreatedAt());
        updatedProd.setShop(product.getShop());
        for(ProductSKU sku : updatedProd.getSkuList()){
            sku.setProduct(updatedProd);
            productService.addProductAttributeList(sku.getAttributes());
        }
        productService.addProductMediaList(updatedProd.getMediaList());
        productService.addProductSKUList(updatedProd.getSkuList());
        productService.saveProduct(updatedProd);
    }

    public Page<ShopProductTableResponseDTO> getProductList(int page, int limit){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        Pageable pageable = PageRequest.of(page, limit);
        Page<Product> productList = productService.findForShopProductTable(shop, pageable);
        return productList.map(
                this::toProductTableDTO
        );
    }

    public void changeProductVisible(String productId) {
        Product product = productService.findById(productId);
        if(product == null){
            throw new RequestException("Invalid request: Product not found");
        }
        product.setVisible(!product.isVisible());
        productService.saveProduct(product);
    }

    private ShopProductTableResponseDTO toProductTableDTO(Product product) {
        ShopProductTableResponseDTO dto = new ShopProductTableResponseDTO();
        dto.setId(product.getId().toString());
        dto.setName(product.getName());
        dto.setThumbnailUrl(product.getThumbnailUrl());
        dto.setVisible(product.isVisible());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        ProductSaleInfo saleInfo = orderService.getProductSaleInfo(product.getId());
        dto.setRevenue(saleInfo.getRevenue());
        dto.setSold(saleInfo.getSold());
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

    private ShopAddress fromDTOtoAddress(AddAddressDTO dto){
        ShopAddress address = new ShopAddress();
        address.setSenderName(dto.getSenderName());
        address.setPhoneNumber(dto.getPhoneNumber());
        address.setDetail(dto.getDetail());
        Province province = addressService.findProvinceByName(dto.getProvince());
        if(province == null){
            throw new RequestException("Địa chỉ không hợp lệ");
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
            throw new RequestException("Địa chỉ không hợp lệ");
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
            throw new RequestException("Địa chỉ không hợp lệ");
        }
        return address;
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

    private ShopProfileDTO toShopProfileDTO(Shop shop){
        ShopProfileDTO dto = new ShopProfileDTO();
        dto.setId(shop.getId().toString());
        dto.setShopName(shop.getName());
        dto.setDescription(shop.getDescription());
        dto.setEmail(shop.getUser().getEmail());
        dto.setAvatarUrl(shop.getAvatarUrl());
        dto.setPhoneNumber(shop.getUser().getPhoneNumber());
        dto.setCreatedAt(shop.getCreatedAt());
        return dto;
    }

}