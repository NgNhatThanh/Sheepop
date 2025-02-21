package com.app.bdc_backend.model.product;

import com.app.bdc_backend.model.enums.MediaType;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_review_medias")
@Getter
@Setter
public class ProductReviewMedia {

    @Id
    private ObjectId id;

    private MediaType type;

    private String url;

}
