package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.product.ProductAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderItemDTO {

    private String id;

    private ProductDTO product = new ProductDTO();

    private int quantity;

    private long price;

    private List<ProductAttribute> attributes;

    @Getter
    @Setter
    public static class ProductDTO{

        private String id;

        private String name;

        private String thumbnailUrl;

    }

}
