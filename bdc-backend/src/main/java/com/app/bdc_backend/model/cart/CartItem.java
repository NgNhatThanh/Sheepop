package com.app.bdc_backend.model.cart;

import com.app.bdc_backend.model.product.Product;
import com.app.bdc_backend.model.product.ProductAttribute;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "cart_items")
@Getter
@Setter
public class CartItem {

    @Id
    private ObjectId id;

    @DocumentReference
    @JsonIgnore
    private Cart cart;

    @DocumentReference
    private Product product;

    private List<ProductAttribute> attributes;

    private long price;

    private int quantity;

    private boolean selected = true;

}
