package com.app.bdc_backend.model.dto;

import com.app.bdc_backend.model.product.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductPageImpl {

    private long totalElements;

    private List<Product> content;

}
