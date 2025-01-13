package com.app.bdc_backend.model.order;

import com.app.bdc_backend.model.Address;
import com.app.bdc_backend.model.User;
import lombok.AllArgsConstructor;
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

    private Address address;

    private Payment payment;

    private long total;

    private Date createdAt;

}
