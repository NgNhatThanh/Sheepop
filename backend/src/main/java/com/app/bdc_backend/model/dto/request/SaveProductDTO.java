package com.app.bdc_backend.model.dto.request;

import com.app.bdc_backend.model.product.Category;
import com.app.bdc_backend.model.product.ProductMedia;
import com.app.bdc_backend.model.product.ProductSKU;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SaveProductDTO {

    @NotBlank
    private String productId;

    @NotBlank(message = "Product's name cannot be empty")
    private String name;

    @NotBlank(message = "Product's description cannot be empty")
    private String description;

    @NotBlank
    private String thumbnailUrl;

    @Min(value = 0)
    private long price;

    @Min(value = 0)
    private int quantity;

    private Category category;

    private List<ProductSKU> skuList;

    private List<ProductMedia> mediaList;

    @Min(value = 1)
    private int weight;

    private boolean visible;

}
