package com.app.bdc_backend.model.shop;

import com.app.bdc_backend.model.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;

@Document(collection = "shops")
@Getter
@Setter
@ToString
public class Shop {

    @Id
    private ObjectId id;

    private String name;

    private String description;

    private String avatarUrl;

    @DocumentReference
    private User user;

    private Date createdAt;

    private boolean active = true;

    private boolean deleted = false;

}
