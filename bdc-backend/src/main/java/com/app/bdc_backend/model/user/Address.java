package com.app.bdc_backend.model.user;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "addresses")
@Getter
@Setter
public class Address {

    @Id
    private ObjectId id;

    private String title;

    private String receiverName;

    private String phoneNumber;

    private String city;

    private String district;

    private String ward;

    private String detail;

}
