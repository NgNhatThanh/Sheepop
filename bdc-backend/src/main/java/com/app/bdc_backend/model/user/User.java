package com.app.bdc_backend.model.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "users")
@Setter
@Getter
public class User {

    @Id
    private ObjectId id;

    private String fullName;

    private String username;

    private String password;

    private String email;

    private String phoneNumber;

    private String avatarUrl;

    private Date createdAt;

    private Date dob;

    private List<Address> addressList = new ArrayList<>();

}
