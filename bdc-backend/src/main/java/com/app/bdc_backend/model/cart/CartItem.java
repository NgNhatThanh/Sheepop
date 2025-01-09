package com.app.bdc_backend.model.cart;

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

@Document(collection = "cart_items")
@Getter
@Setter
@AllArgsConstructor
public class CartItem {

    @Id
    private ObjectId id;

    @DocumentReference
    private Cart cart;

    private ProductSKU productSKU;

    private int quantity;

    private Date updatedAt;

}
