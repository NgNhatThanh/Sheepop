package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.product.ProductAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductSKUResponseDTO {

    private String sku;

    private int price;

    private int quantity;

    private List<ProductAttribute> attributes;

}
