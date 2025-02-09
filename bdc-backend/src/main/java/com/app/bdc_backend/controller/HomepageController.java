package com.app.bdc_backend.controller;

import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.ProductCardDTO;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.OptionalLong;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/homepage")
public class HomepageController {

    private final ProductService productService;

    @GetMapping("/get-items")
    public ResponseEntity<?> getItems(@RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "limit", defaultValue = "50") int limit){
        Page<Product> productPage = productService.findAllForHomepage(page, limit);
        PageResponse<ProductCardDTO> response = new PageResponse<>(
                productPage.map(this::toProductCardDTO)
        );
        return ResponseEntity.ok(response);
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
        return dto;
    }

}
