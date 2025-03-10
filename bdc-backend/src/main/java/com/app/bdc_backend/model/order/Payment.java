package com.app.bdc_backend.model.order;

import com.app.bdc_backend.model.enums.PaymentStatus;
import com.app.bdc_backend.model.enums.PaymentType;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "payments")
@Getter
@Setter
public class Payment {

    @Id
    private ObjectId id;

    private long amount;

    private PaymentType type;

    private PaymentStatus status;

    private Date expireAt;

    private Date createdAt = new Date();

    private Date updatedAt;

}
