package com.app.bdc_backend.model.product;

import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {

    @Id
    @EqualsAndHashCode.Include
    private ObjectId id;

    @EqualsAndHashCode.Include
    private String name;

    @DocumentReference
    private Category parent;

    private String description;

    private boolean hasChildren;

    private int productCount;

    private Date createdAt;

}
