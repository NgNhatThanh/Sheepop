package com.app.bdc_backend.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;

@Document(collection = "product_skus")
@Getter
@Setter
@AllArgsConstructor
public class ProductSKU {

    @Id
    private ObjectId id;

    private Product product;

    private String sku;

    private long listedPrice;

    private long actualPrice;

    private int quantity;

    private Date updatedAt;

    private ProductAttribute attribute;

}
