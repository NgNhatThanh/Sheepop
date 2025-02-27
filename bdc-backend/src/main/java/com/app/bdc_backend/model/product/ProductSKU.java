package com.app.bdc_backend.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@Document(collection = "product_skus")
@Getter
@Setter
@ToString
public class ProductSKU {

    @Id
    private ObjectId id;

    @JsonIgnore
    private Product product;

    private String sku;

    private long price;

    private int quantity;

    private List<ProductAttribute> attributes;

}
