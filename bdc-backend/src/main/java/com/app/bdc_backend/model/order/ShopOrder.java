package com.app.bdc_backend.model.order;

import com.app.bdc_backend.model.enums.ShopOrderStatus;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@Document(collection = "shop_orders")
@Getter
@Setter
public class ShopOrder {

    @Id
    private ObjectId id;

    @DocumentReference
    private User user;

    @DocumentReference
    private Order order;

    @DocumentReference
    private Shop shop;

    @DocumentReference
    private List<OrderItem> items;

    private int shippingFee;

    private int status = ShopOrderStatus.PENDING;

    private Date createdAt = new Date();

}
