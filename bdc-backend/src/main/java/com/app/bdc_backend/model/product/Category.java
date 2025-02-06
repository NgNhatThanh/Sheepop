package com.app.bdc_backend.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;

@Document(collection = "categories")
@Getter
@Setter
public class Category {

    @Id
    private ObjectId id;

    private String name;

    @DocumentReference
    private Category parent;

    private String description;

    private Date createdAt;

}
