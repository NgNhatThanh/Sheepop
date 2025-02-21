package com.app.bdc_backend.model.product;

import com.app.bdc_backend.model.order.OrderItem;
import com.app.bdc_backend.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "product_reviews")
@Getter
@Setter
public class ProductReview {

    @Id
    private ObjectId id;

    @DocumentReference
    private User reviewer;

    @DocumentReference
    private OrderItem orderItem;

    private int rating;

    private String content;

    private int reactionCount;

    private List<ProductReviewMedia> mediaList = new ArrayList<>();

    private Date createdAt;

}
