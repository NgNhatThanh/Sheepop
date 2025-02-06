package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.ProductAttribute;
import com.app.bdc_backend.model.product.ProductMedia;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductResponseDTO {

    private String id;

    private String name;

    private String shopId;

    private String description;

    private String thumbnailUrl;

    private long price;

    private int quantity;

    private Category category;

    private List<VariationDisplayIndicator> variationDisplayIndicators = new ArrayList<>();

    private List<ProductMedia> mediaList;

}
