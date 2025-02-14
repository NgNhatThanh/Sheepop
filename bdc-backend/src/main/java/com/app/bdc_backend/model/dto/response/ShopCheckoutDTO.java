package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.product.ProductAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ShopCheckoutDTO {

    private ShopDTO shop;

    private List<ShopCheckoutItem> items = new ArrayList<>();

    private int shipmentFee;

    private Date expectedDeliveryDate;

    @Getter
    @Setter
    public static class ShopDTO {

        private String id;

        private String username;

        private String name;

    }

    @Getter
    @Setter
    public static class ShopCheckoutItem {

        private String itemId;

        private String productId;

        private String name;

        private String thumbnailUrl;

        private long price;

        private int quantity;

        private int stock;

        private String sku;

        private List<ProductAttribute> attributes;

        private boolean selected;

    }

}
