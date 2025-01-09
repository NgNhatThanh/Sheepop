package com.app.bdc_backend.model.product;

import com.app.bdc_backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;

@Document(collection = "product_reviews")
@Getter
@Setter
@AllArgsConstructor
public class ProductReview {

    @Id
    private ObjectId id;

    @DocumentReference
    private User reviewer;

    private ProductSKU productSKU;

    private int rating;

    private String content;

    private Date createdAt;

    private int reactionCount;

}
