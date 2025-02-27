package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.DataNotExistException;
import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.address.Ward;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.request.SaveProductDTO;
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

    private final ProductFacadeService productFacadeService;

    public ProductResponseDTO previewProduct(String productId) throws RuntimeException {
        Product product = productService.findById(productId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!product.getShop().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Invalid request: not your shop's product");
        }
        return productFacadeService.getProduct(productId, true);
    }

    public Product getProductForEdit(String productId){
        return productService.findById(productId);
    }

    public ShopAddress addAddress(AddAddressDTO addressDTO) {
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
        return address;
    }

    public void deleteProduct(String productId) throws RuntimeException {
        Product product = productService.findById(productId);
        if(product == null)
            throw new DataNotExistException("Invalid reques: Product not found");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!product.getShop().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Invalid request: not your shop's product");
        }
        productService.delete(product);
    }

    public Page<ShopOrderDTO> getShopOrders(int type,
                                            int filterType,
                                            String keyword,
                                            int page,
                                            int limit) throws Exception{
        if(filterType < 0 || filterType > 3){
            throw new Exception("Invalid request: filter type");
        }
        if(filterType >= 1 && (keyword == null || keyword.isEmpty())){
            throw new Exception("Invalid request: filter data");
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
            throw new Exception("Invalid request");
        }
        return data.map(this::toShopOrderDTO);
    }

    public void updateOrder(String shopOrderId,
                            int currentStatus) throws Exception{
        if(currentStatus > 2){
            throw new Exception("Invalid request: status");
        }
        ShopOrder shopOrder = orderService.getShopOrderById(shopOrderId);
        if(shopOrder == null){
            throw new Exception("Invalid shop order id");
        }
        if(shopOrder.getStatus() != currentStatus){
            throw new Exception( "Invalid current status");
        }
        if(currentStatus == ShopOrderStatus.PENDING){
            shopOrder.setStatus(ShopOrderStatus.PREPARING);
        }
        else{
            shopOrder.setStatus(ShopOrderStatus.DELIVERING);
        }
        orderService.saveAllShopOrders(List.of(shopOrder));
    }

    public void addProduct(SaveProductDTO productDTO) throws RuntimeException {
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
    }

    public void updateProduct(SaveProductDTO dto) throws RuntimeException {
        Product product = productService.findById(dto.getProductId());
        if(product == null){
            throw new DataNotExistException("Invalid request: product not found");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!product.getShop().getUser().getUsername().equals(username)){
            throw new RuntimeException("Invalid request: not your shop's product");
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

    public void changeProductVisible(String productId) throws RuntimeException{
        Product product = productService.findById(productId);
        if(product == null){
            throw new DataNotExistException("Invalid request: Product not found");
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

    private ShopAddress fromDTOtoAddress(AddAddressDTO dto) throws RuntimeException{
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

}