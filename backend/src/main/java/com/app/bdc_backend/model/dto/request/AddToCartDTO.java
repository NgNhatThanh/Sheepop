package com.app.bdc_backend.model.dto.request;

import com.app.bdc_backend.model.product.ProductAttribute;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddToCartDTO {

    @NotBlank
    private String productId;

    @Min(1)
    private int quantity;

    @NotNull
    private List<ProductAttribute> attributes;

}
