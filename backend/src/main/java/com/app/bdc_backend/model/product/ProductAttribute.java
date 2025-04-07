package com.app.bdc_backend.model.product;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_attributes")
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
public class ProductAttribute {

    @Id
    private ObjectId id;

    private String name;

    private String value;

}
