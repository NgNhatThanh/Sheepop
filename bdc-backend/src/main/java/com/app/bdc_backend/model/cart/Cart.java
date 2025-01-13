package com.app.bdc_backend.model.cart;

import com.app.bdc_backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "carts")
@Getter
@Setter
public class Cart {

    @Id
    private ObjectId id;

    private User user;

    private Date updatedAt;

    private List<CartItem> items;

}
