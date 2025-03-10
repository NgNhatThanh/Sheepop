package com.app.bdc_backend.model.dto.request;

import com.app.bdc_backend.model.product.ProductAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddToCartDTO {

    private String productId;

    private int quantity;

    private List<ProductAttribute> attributes;

}
