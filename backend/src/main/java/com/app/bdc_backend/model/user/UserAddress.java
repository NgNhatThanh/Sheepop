package com.app.bdc_backend.model.user;

import com.app.bdc_backend.model.address.Address;
import com.app.bdc_backend.model.address.District;
import com.app.bdc_backend.model.address.Province;
import com.app.bdc_backend.model.address.Ward;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "user_addresses")
@Getter
@Setter
public class UserAddress extends Address {

    @Id
    private ObjectId id;

    private String username;

    private String receiverName;

    private String phoneNumber;

    private boolean primary;

}
