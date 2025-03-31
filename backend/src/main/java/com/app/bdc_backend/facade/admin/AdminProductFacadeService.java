package com.app.bdc_backend.facade.admin;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.Notification;
import com.app.bdc_backend.model.dto.response.AdminProductDTO;
import com.app.bdc_backend.model.enums.NotificationScope;
import com.app.bdc_backend.model.enums.RestrictStatus;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.service.NotificationService;
import com.app.bdc_backend.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminProductFacadeService {

    private final ProductService productService;

    private final NotificationService notificationService;

    public Page<AdminProductDTO> getProductList(int type,
                                                String productName,
                                                String shopName,
                                                int page,
                                                int limit){
        Page<Product> res;
        if(type == 0){
            res = productService.getActiveProductsForAdmin(productName, shopName, page, limit);
        }
        else if(type == 1){
            res = productService.getRestrictedProductsForAdmin(productName, shopName, page, limit);
        }
        else if(type == 2){
            res = productService.getPendingRestrictProductsForAdmin(productName, shopName, page, limit);
        }
        else{
            throw new RequestException("Invalid request: list type");
        }
        return res.map(this::toAdminProductDTO);
    }

    public Product getDetailProduct(String productId){
        Product product = productService.findById(productId);
        if(product == null){
            throw new RequestException("Invalid request: product not found");
        }
        return product;
    }

    public void restrictProduct(String productId, String reason){
        if(productId == null || reason == null || reason.isEmpty())
            throw new RequestException("Invalid request: empty data passed");
        Product product = productService.findById(productId);
        if(product == null){
            throw new RequestException("Invalid request: product not found");
        }
        if(product.getRestrictStatus() != null && product.getRestrictStatus() == RestrictStatus.RESTRICTED){
            throw new RequestException("Invalid request: invalid restrict status");
        }
        product.setRestricted(true);
        product.setRestrictStatus(RestrictStatus.RESTRICTED);
        product.setDeleted(true);
        product.setRestrictedReason(reason);
        productService.saveProduct(product);

        Notification notification = Notification.builder()
                .scope(NotificationScope.SHOP)
                .itemCount(1)
                .receiver(product.getShop().getUser())
                .content("%d sản phẩm bị đình chỉ")
                .redirectUrl("/myshop/product-list?type=1")
                .thumbnailUrl(product.getThumbnailUrl())
                .build();
        notificationService.sendNotification(notification);
    }

    public void openRestrict(String productId){
        Product product = productService.findById(productId);
        if(product == null){
            throw new RequestException("Invalid request: product not found");
        }
        product.setRestricted(false);
        product.setRestrictStatus(RestrictStatus.OPENED);
        product.setDeleted(false);
        productService.saveProduct(product);

        Notification notification = Notification.builder()
                .scope(NotificationScope.SHOP)
                .itemCount(1)
                .receiver(product.getShop().getUser())
                .content("%d sản phẩm được gỡ đình chỉ")
                .redirectUrl("/myshop/product-list?type=0")
                .thumbnailUrl(product.getThumbnailUrl())
                .build();
        notificationService.sendNotification(notification);
    }

    private AdminProductDTO toAdminProductDTO(Product product){
        AdminProductDTO dto = new AdminProductDTO();
        dto.setShopName(product.getShop().getName());
        dto.setUsername(product.getShop().getUser().getUsername());
        dto.setId(product.getId().toString());
        dto.setThumbnailUrl(product.getThumbnailUrl());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setDeleted(product.isDeleted());
        dto.setVisible(product.isVisible());
        dto.setRestricted(product.isRestricted());
        if(product.isRestricted()){
            dto.setRestrictReason(product.getRestrictedReason());
            dto.setRestrictStatus(product.getRestrictStatus());
        }
        return dto;
    }

}
