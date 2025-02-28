package com.app.bdc_backend.model.user;

import com.app.bdc_backend.model.enums.Gender;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "users")
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @EqualsAndHashCode.Include
    private ObjectId id;

    private String fullName;

    @EqualsAndHashCode.Include
    private String username;

    private String password;

    private String email;

    private String phoneNumber;

    private String avatarUrl;

    private Gender gender;

    private Date createdAt;

    @DocumentReference
    private Role role;

    private boolean fromSocial = false;

    private Date dob;

    private boolean deleted = false;

}
