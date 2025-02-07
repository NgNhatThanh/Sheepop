package com.app.bdc_backend.model.cart;

import com.app.bdc_backend.model.user.User;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "carts")
@Getter
@Setter
public class Cart {

    @Id
    private ObjectId id;

    @DocumentReference
    private User user;

    private Date updatedAt;

    @DocumentReference
    private List<CartItem> items = new ArrayList<>();

}
