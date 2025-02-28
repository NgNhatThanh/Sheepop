package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
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
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductFacadeService {

    private final ProductService productService;

    private final ReviewService reviewService;

    private final OrderService orderService;

    public ProductResponseDTO getProduct(String productId, boolean preview) throws RequestException{
        Product product = productService.findById(productId);
        if(product == null || (!preview && !product.isVisible())) {
            throw new RequestException("Product not found");
        }
        return toProductResponseDTO(product);
    }

    public SelectVariationResponseDTO selectVariation(SelectVariationDTO selectVariationDTO) throws RequestException{
        Product product = productService.findById(selectVariationDTO.getProductId());
        if(product == null || !product.isVisible() || product.getSkuList().isEmpty()) {
            throw new RequestException("Product not found");
        }
        return toSelectVariationResponseDTO(product, selectVariationDTO);
    }

    private SelectVariationResponseDTO toSelectVariationResponseDTO(Product product, SelectVariationDTO requestDTO) {
        SelectVariationResponseDTO dto = new SelectVariationResponseDTO();
        boolean fullAttribute = (requestDTO.getAttributes().size() == product.getSkuList().get(0).getAttributes().size());
        Map<String, Map<String, Integer>> variations = new HashMap<>();
        for(ProductSKU sku : product.getSkuList()) {
            setAttrValue(variations, sku);
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

        setVariationDisplayIndicator(variations, dto.getVariationDisplayIndicators());
        return dto;
    }

    private void setVariationDisplayIndicator(Map<String, Map<String, Integer>> variations, List<VariationDisplayIndicator> variationDisplayIndicators ) {
        for(String name : variations.keySet()) {
            VariationDisplayIndicator e = new VariationDisplayIndicator();
            e.setName(name);
            for(String value : variations.get(name).keySet()) {
                VariationDisplayIndicator.VariationOption option = new VariationDisplayIndicator.VariationOption();
                option.setValue(value);
                option.setAvailable(variations.get(name).get(value) != 0);
                e.getVariationOptions().add(option);
            }
            variationDisplayIndicators.add(e);
        }
    }

    private void setAttrValue(Map<String, Map<String, Integer>> variations, ProductSKU sku) {
        for(ProductAttribute attr : sku.getAttributes()) {
            variations.putIfAbsent(attr.getName(), new HashMap<>());
            variations.get(attr.getName()).putIfAbsent(attr.getValue(), 0);
            variations.get(attr.getName()).put(attr.getValue(), variations.get(attr.getName()).get(attr.getValue()) + sku.getQuantity());
        }
    }

    private ProductResponseDTO toProductResponseDTO(Product product) {
        ProductResponseDTO dto = ModelMapper.getInstance().map(product, ProductResponseDTO.class);
        dto.setId(product.getId().toString());
        dto.setShopId(product.getShop().getId().toString());
        Map<String, Map<String, Integer>> variations = new HashMap<>();
        long minPrice = (product.getPrice() == -1 ? 99999999999999L : product.getPrice());
        int sumQuantity = 0;
        for(ProductSKU sku : product.getSkuList()) {
            setAttrValue(variations, sku);
            sumQuantity += sku.getQuantity();
            minPrice = Math.min(minPrice, sku.getPrice());
        }
        dto.setQuantity(Math.max(sumQuantity, product.getQuantity()));
        dto.setPrice(minPrice);
        setVariationDisplayIndicator(variations, dto.getVariationDisplayIndicators());

        BasicReviewInfo productReviewInfo = reviewService.getProductReviewInfo(product.getId());
        BasicReviewInfo shopReviewInfo = reviewService.getShopReviewInfo(product.getShop().getId());
        int soldCount = orderService.countProductSold(product.getId());
        int shopProductCount = productService.countProductOfShop(product.getShop().getId());
        dto.setAverageRating(productReviewInfo.getAverageRating());
        dto.setTotalReviews(productReviewInfo.getTotalReviews());
        dto.setSoldCount(soldCount);

        dto.getShop().setId(product.getShop().getId().toString());
        dto.getShop().setName(product.getShop().getName());
        dto.getShop().setAvatarUrl(product.getShop().getAvatarUrl());
        dto.getShop().setAverageRating(shopReviewInfo.getAverageRating());
        dto.getShop().setTotalReviews(shopReviewInfo.getTotalReviews());
        dto.getShop().setCreatedAt(product.getShop().getCreatedAt());
        dto.getShop().setTotalProducts(shopProductCount);
        return dto;
    }

}