package com.app.bdc_backend.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "products")
@Getter
@Setter
@AllArgsConstructor
public class Product {

    @Id
    private ObjectId id;

    private String name;

    private String description;

    private String thumbnailUrl;

    @DocumentReference
    private List<ProductSKU> productSKUList;

    @DocumentReference
    private List<ProductMedia> productMediaList;

}
