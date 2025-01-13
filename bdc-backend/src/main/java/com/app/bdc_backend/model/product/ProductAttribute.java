package com.app.bdc_backend.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_attributes")
@Getter
@Setter
public class ProductAttribute {

    @Id
    private ObjectId id;

    private String name;

    private String value;

}
