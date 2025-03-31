package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.Notification;
import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.address.Ward;
import com.app.bdc_backend.model.dto.request.AddAddressDTO;
import com.app.bdc_backend.model.dto.request.SaveProductDTO;
import com.app.bdc_backend.model.dto.request.UpdateShopProfileDTO;
import com.app.bdc_backend.model.dto.response.*;
import com.app.bdc_backend.model.enums.NotificationScope;
import com.app.bdc_backend.model.enums.PaymentStatus;
import com.app.bdc_backend.model.enums.RestrictStatus;
import com.app.bdc_backend.model.enums.ShopOrderStatus;
import com.app.bdc_backend.model.order.ShopOrder;
import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.shop.ShopAddress;
import com.app.bdc_backend.model.shop.ShopCategories;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.*;
import com.app.bdc_backend.service.product.CategoryService;
import com.app.bdc_backend.service.product.ProductService;
import com.app.bdc_backend.service.user.ShopService;
import com.app.bdc_backend.service.user.UserService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShopFacadeService {

    private final UserService userService;

    private final ShopService shopService;

    private final AddressService addressService;

    private final ProductService productService;

    private final OrderService orderService;

    private final CategoryService categoryService;

    private final ProductFacadeService productFacadeService;

    private final NotificationService notificationService;

    public ShopProfileDTO getShopProfile(String username) {
        User user = userService.findByUsername(username);
        if(user == null)
            throw new RequestException("User not found");
        Shop shop = shopService.findByUser(user);
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
        List<Product> products = productService.getAllByShop(shop);
        for(Product product : products){
            product.setLocation(address.getProvince().getName());
        }
        productService.saveAllProducts(products);
        return address;
    }

    @Transactional
    public void deleteProduct(String productId) {
        Product product = productService.findById(productId);
        if(product == null)
            throw new RequestException("Invalid reques: Product not found");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!product.getShop().getUser().getUsername().equals(username)) {
            throw new RequestException("Invalid request: not your shop's product");
        }
        product.getShop().setProductCount(product.getShop().getProductCount() - 1);
        shopService.save(product.getShop());
        productService.delete(product);
    }

    public Page<ShopOrderDTO> getShopOrders(int type,
                                            int filterType,
                                            String keyword,
                                            int sortType,
                                            int page,
                                            int limit){
        if(filterType < 0 || filterType > 3){
            throw new RequestException("Invalid request: filter type");
        }
        if(filterType >= 1 && (keyword == null || keyword.isEmpty())){
            throw new RequestException("Invalid request: filter data");
        }
        if(sortType < 0 || sortType > 3)
            throw new RequestException("Invalid request: sort type");
        Sort sort;
        String sortBy;
        if(sortType < 2) sortBy = "createdAt";
        else sortBy = "total";
        sort = Sort.by(sortType % 2 == 0 ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        Pageable pageable = PageRequest.of(page, limit, sort);
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
        return data.map(orderService::toShopOrderDTO);
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
        if(shopOrder.getOrder().getPayment().getStatus() == PaymentStatus.PENDING)
            throw new RequestException("Order's payment hasn't been done yet");
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

        String notiContent;
        if(shopOrder.getStatus() == ShopOrderStatus.PREPARING){
            notiContent = "Đơn hàng đã được xác nhận";
        }
        else{
            notiContent = "Đơn hàng đang được vận chuyển";
        }
        Notification notification = Notification.builder()
                .scope(NotificationScope.BUYER)
                .content(notiContent)
                .receiver(shopOrder.getUser())
                .redirectUrl("/account/orders/" + shopOrderId)
                .thumbnailUrl(shopOrder.getItems().get(0).getProduct().getThumbnailUrl())
                .build();
        notificationService.sendNotification(notification);
    }

    @Transactional
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
        product.setUpdatedAt(new Date());
        product.setLocation(address.getProvince().getName());
        Category category = ModelMapper.getInstance().map(productDTO.getCategory(), Category.class);
        if(category.isHasChildren())
            throw new RequestException("Invalid request: category has children");
        product.setCategory(category);
        for(ProductSKU sku : product.getSkuList()){
            sku.setProduct(product);
            productService.addProductAttributeList(sku.getAttributes());
        }
        shop.setProductCount(shop.getProductCount() + 1);
        saveShopCategories(category, shop);
        shopService.save(shop);
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
        Category category = ModelMapper.getInstance().map(dto.getCategory(), Category.class);
        if(category.isHasChildren())
            throw new RequestException("Invalid request: category has children");
        Shop shop = product.getShop();
        saveShopCategories(category, shop);
        for(ProductSKU sku : updatedProd.getSkuList()){
            sku.setProduct(updatedProd);
            productService.addProductAttributeList(sku.getAttributes());
        }
        productService.addProductMediaList(updatedProd.getMediaList());
        productService.addProductSKUList(updatedProd.getSkuList());
        if(product.isRestricted()){
            updatedProd.setRestrictedReason(product.getRestrictedReason());
            updatedProd.setRestrictStatus(RestrictStatus.PENDING);
            updatedProd.setRestricted(true);
        }

        updatedProd.setDeleted(product.isDeleted());
        productService.saveProduct(updatedProd);
    }

    private void saveShopCategories(Category category, Shop shop) {
        ShopCategories shopCategories = shopService.getShopCategories(shop);
        boolean existedCat = false;
        for(Category cat : shopCategories.getCategories()){
            if(cat.getId().equals(category.getId())){
                existedCat = true;
                break;
            }
        }
        if(!existedCat){
            shopCategories.getCategories().add(category);
            shopService.saveShopCategories(shopCategories);
        }
    }

    public Page<ShopProductTableResponseDTO> getProductList(int type,
                                                            String keyword,
                                                            String categoryId,
                                                            int sortType,
                                                            int page,
                                                            int limit){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        if(!shop.getUser().getUsername().equals(username))
            throw new RequestException("Invalid request: not your shop's product");
        if(sortType < 0 || sortType > 9)
            throw new RequestException("Invalid sort type");
        Sort sort;
        String sortBy;
        if(sortType < 2) sortBy = "revenue";
        else if(sortType < 4) sortBy = "quantity";
        else if(sortType < 6) sortBy = "price";
        else if(sortType < 8) sortBy = "createdAt";
        else sortBy = "sold";
        sort = Sort.by(sortType % 2 == 0 ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, limit, sort);
        Category category = null;
        if(!categoryId.isEmpty()) {
            category = categoryService.getById(categoryId);
            if(category == null)
                throw new RequestException("Invalid request: category not found");
        }
        Page<Product> products;
        if(type == 0){
            products = productService.getActiveProductsForShop(shop, keyword, category, pageable);
        }
        else if(type == 1){
            products = productService.getRestrictedProductForShop(shop, keyword, category, pageable);
        }
        else if(type == 2){
            products = productService.getPendingRestrictProductsForShop(shop, keyword, category, pageable);
        }
        else if(type == 3){
            products = productService.getHiddenProductsForShop(shop, keyword, category, pageable);
        }
        else if(type == 4){
            products = productService.getOutOfStockProductsForShop(shop, keyword, category, pageable);
        }
        else{
            throw new RequestException("Invalid request: invalid type");
        }
        return products.map(
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

    public List<Category> getShopCategories(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Shop shop = shopService.findByUser(user);
        return shopService.getShopCategories(shop).getCategories();
    }

    private ShopProductTableResponseDTO toProductTableDTO(Product product) {
        ShopProductTableResponseDTO dto = new ShopProductTableResponseDTO();
        dto.setId(product.getId().toString());
        dto.setName(product.getName());
        dto.setThumbnailUrl(product.getThumbnailUrl());
        dto.setVisible(product.isVisible());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setRevenue(product.getRevenue());
        dto.setSold(product.getSold());
        if(!product.getSkuList().isEmpty()){
            long minPrice = 999999999999L;
            for(ProductSKU sku : product.getSkuList()){
                dto.setQuantity(dto.getQuantity() + sku.getQuantity());
                dto.getSkuList().add(ModelMapper.getInstance().map(sku, ProductSKUResponseDTO.class));
                minPrice = Math.min(minPrice, sku.getPrice());
            }
            dto.setPrice(minPrice);
        }
        else{
            dto.setQuantity(product.getQuantity());
            dto.setPrice(product.getPrice());
        }
        dto.setRestricted(product.isRestricted());
        dto.setRestrictReason(product.getRestrictedReason());
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