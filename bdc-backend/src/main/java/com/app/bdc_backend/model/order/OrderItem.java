package com.app.bdc_backend.model.order;

import com.app.bdc_backend.model.enums.OrderStatus;
import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductSKU;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;

@Document(collection = "order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    private ObjectId id;

    @DocumentReference
    private Order order;

    private Product product;

    private OrderStatus status;

    private int quantity;

    private Date updatedAt;

}
