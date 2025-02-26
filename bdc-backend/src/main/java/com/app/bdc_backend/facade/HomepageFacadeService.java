package com.app.bdc_backend.facade;

import com.app.bdc_backend.model.dto.BasicReviewInfo;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.ProductCardDTO;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.service.OrderService;
import com.app.bdc_backend.service.ProductService;
import com.app.bdc_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.OptionalLong;

@Component
@RequiredArgsConstructor
public class HomepageFacadeService {

    private final ProductService productService;

    private final ReviewService reviewService;

    private final OrderService orderService;

    public Page<ProductCardDTO> getHomePageItems(int page, int limit){
        Page<Product> productPage = productService.findAllForHomepage(page, limit);
        Page<ProductCardDTO> cardDTOS = productPage.map(this::toProductCardDTO);
        return cardDTOS;
    }

    private ProductCardDTO toProductCardDTO(Product product){
        ProductCardDTO dto = new ProductCardDTO();
        dto.setName(product.getName());
        dto.setThumbnailUrl(product.getThumbnailUrl());
        dto.setId(product.getId().toString());
        if(product.getSkuList().isEmpty()) dto.setPrice(product.getPrice());
        else{
            OptionalLong minPrice = product.getSkuList()
                    .stream()
                    .mapToLong(ProductSKU::getPrice)
                    .min();
            dto.setPrice(minPrice.getAsLong());
        }
        BasicReviewInfo reviewInfo = reviewService.getProductReviewInfo(product.getId());
        dto.setAverageRating(reviewInfo.getAverageRating());
        int sold = orderService.countProductSold(product.getId());
        dto.setSold(sold);
        return dto;
    }

}
