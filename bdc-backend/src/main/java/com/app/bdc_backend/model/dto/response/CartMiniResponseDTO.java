package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.cart.CartItem;
import com.app.bdc_backend.model.product.ProductAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CartMiniResponseDTO {

    private List<MiniCartItem> items;

    private Date updatedAt;

    @Getter
    @Setter
    public static class MiniCartItem{

        private String name;

        private String thumbnailUrl;

        private long price;

        private int quantity;

        private List<ProductAttribute> attributes;

    }

}
