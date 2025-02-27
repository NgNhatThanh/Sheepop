package com.app.bdc_backend.model.order;

import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductAttribute;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@Document(collection = "order_items")
@Getter
@Setter
public class OrderItem {

    @Id
    private ObjectId id;

    @DocumentReference
    private Product product;

    private int quantity;

    private long price;

    private boolean success;

    private List<ProductAttribute> attributes;

    private Date updatedAt;

}
