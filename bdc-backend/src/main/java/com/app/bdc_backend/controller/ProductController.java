package com.app.bdc_backend.controller;

import com.app.bdc_backend.model.dto.BasicReviewInfo;
import com.app.bdc_backend.model.dto.request.SelectVariationDTO;
import com.app.bdc_backend.model.dto.response.ProductResponseDTO;
import com.app.bdc_backend.model.dto.response.SelectVariationResponseDTO;
import com.app.bdc_backend.model.dto.response.VariationDisplayIndicator;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductAttribute;
import com.app.bdc_backend.model.product.ProductSKU;
import com.app.bdc_backend.service.OrderService;
import com.app.bdc_backend.service.ProductService;
import com.app.bdc_backend.service.ReviewService;
import com.app.bdc_backend.service.ShopService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService productService;

    private final ReviewService reviewService;

    private final OrderService orderService;

    private final ShopService shopService;

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable("productId") String productId) {
        Product product = productService.findById(productId);
        if(product == null || !product.isVisible()) {
            return ResponseEntity.notFound().build();
        }
        ProductResponseDTO responseDTO = toProductResponseDTO(product);
        BasicReviewInfo productReviewInfo = reviewService.getProductReviewInfo(product.getId());
        BasicReviewInfo shopReviewInfo = reviewService.getShopReviewInfo(product.getShop().getId());
        int soldCount = orderService.countProductSold(product.getId());
        int shopProductCount = productService.countProductOfShop(product.getShop().getId());
        responseDTO.setAverageRating(productReviewInfo.getAverageRating());
        responseDTO.setTotalReviews(productReviewInfo.getTotalReviews());
        responseDTO.setSoldCount(soldCount);

        responseDTO.getShop().setId(product.getShop().getId().toString());
        responseDTO.getShop().setName(product.getShop().getName());
        responseDTO.getShop().setAvatarUrl(product.getShop().getAvatarUrl());
        responseDTO.getShop().setAverageRating(shopReviewInfo.getAverageRating());
        responseDTO.getShop().setTotalReviews(shopReviewInfo.getTotalReviews());
        responseDTO.getShop().setCreatedAt(product.getShop().getCreatedAt());
        responseDTO.getShop().setTotalProducts(shopProductCount);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/select_variation")
    public ResponseEntity<?> selectVariation(@RequestBody SelectVariationDTO selectVariationDTO) {
        Product product = productService.findById(selectVariationDTO.getProductId());
        if(product == null || !product.isVisible() || product.getSkuList().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toSelectVariationResponseDTO(product, selectVariationDTO));
    }

    private SelectVariationResponseDTO toSelectVariationResponseDTO(Product product, SelectVariationDTO requestDTO) {
        SelectVariationResponseDTO dto = new SelectVariationResponseDTO();
        boolean fullAttribute = (requestDTO.getAttributes().size() == product.getSkuList().get(0).getAttributes().size());
        Map<String, Map<String, Integer>> variations = new HashMap<>();
        for(ProductSKU sku : product.getSkuList()) {
            for(ProductAttribute attr : sku.getAttributes()) {
                variations.putIfAbsent(attr.getName(), new HashMap<>());
                variations.get(attr.getName()).putIfAbsent(attr.getValue(), 0);
                variations.get(attr.getName()).put(attr.getValue(), variations.get(attr.getName()).get(attr.getValue()) + sku.getQuantity());
            }
            if(new HashSet<>(sku.getAttributes()).containsAll(requestDTO.getAttributes())) {
                dto.setQuantity(dto.getQuantity() + sku.getQuantity());
                if(fullAttribute) dto.setPrice(sku.getPrice());
            }
        }

        if(!requestDTO.getAttributes().isEmpty()){
            for(ProductSKU sku : product.getSkuList()) {
                if(sku.getQuantity() == 0){
                    for(ProductAttribute rqAttr : requestDTO.getAttributes()) {
                        if(sku.getAttributes().contains(rqAttr)) {
                            for(ProductAttribute attr : sku.getAttributes()) {
                                if(!requestDTO.getAttributes().contains(attr)){
                                    variations.get(attr.getName()).put(attr.getValue(), 0);
                                }
                            }
                        }
                    }
                }
            }
        }

        for(String name : variations.keySet()) {
            VariationDisplayIndicator e = new VariationDisplayIndicator();
            e.setName(name);
            for(String value : variations.get(name).keySet()) {
                VariationDisplayIndicator.VariationOption option = new VariationDisplayIndicator.VariationOption();
                option.setValue(value);
                option.setAvailable(variations.get(name).get(value) != 0);
                e.getVariationOptions().add(option);
            }
            dto.getVariationDisplayIndicators().add(e);
        }
        return dto;
    }

    private ProductResponseDTO toProductResponseDTO(Product product) {
        ProductResponseDTO dto = ModelMapper.getInstance().map(product, ProductResponseDTO.class);
        dto.setId(product.getId().toString());
        dto.setShopId(product.getShop().getId().toString());
        Map<String, Map<String, Integer>> variations = new HashMap<>();
        long minPrice = (product.getPrice() == -1 ? 99999999999999L : product.getPrice());
        int sumQuantity = 0;
        for(ProductSKU sku : product.getSkuList()) {
            for(ProductAttribute attr : sku.getAttributes()) {
                variations.putIfAbsent(attr.getName(), new HashMap<>());
                variations.get(attr.getName()).putIfAbsent(attr.getValue(), 0);
                variations.get(attr.getName()).put(attr.getValue(), variations.get(attr.getName()).get(attr.getValue()) + sku.getQuantity());
            }
            sumQuantity += sku.getQuantity();
            minPrice = Math.min(minPrice, sku.getPrice());
        }
        dto.setQuantity(Math.max(sumQuantity, product.getQuantity()));
        dto.setPrice(minPrice);
        for(String name : variations.keySet()) {
            VariationDisplayIndicator e = new VariationDisplayIndicator();
            e.setName(name);
            for(String value : variations.get(name).keySet()) {
                VariationDisplayIndicator.VariationOption option = new VariationDisplayIndicator.VariationOption();
                option.setValue(value);
                option.setAvailable(variations.get(name).get(value) != 0);
                e.getVariationOptions().add(option);
            }
            dto.getVariationDisplayIndicators().add(e);
        }
        return dto;
    }

}
