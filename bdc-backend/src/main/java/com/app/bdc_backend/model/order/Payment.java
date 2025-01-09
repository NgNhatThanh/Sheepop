package com.app.bdc_backend.model.order;

import com.app.bdc_backend.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "payments")
@Getter
@Setter
@AllArgsConstructor
public class Payment {

    @Id
    private ObjectId id;

    private long amount;

    private String provider;

    private PaymentStatus status;

    private Date createdAt;

    private Date updatedAt;

}
