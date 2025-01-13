package com.app.bdc_backend.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "categories")
@Getter
@Setter
public class Category {

    @Id
    private ObjectId id;

    private String name;

    private Category parent;

    private String description;

    private Date createdAt;

}
