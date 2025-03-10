package com.app.bdc_backend.model.order;

import com.app.bdc_backend.model.enums.OrderStatus;
import com.app.bdc_backend.model.user.UserAddress;
import com.app.bdc_backend.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;

@Document(collection = "orders")
@Getter
@Setter
public class Order {

    @Id
    private ObjectId id;

    @DocumentReference
    private User user;

    @DocumentReference
    private UserAddress address;

    @DocumentReference
    private Payment payment;

    private Date createdAt = new Date();

}
