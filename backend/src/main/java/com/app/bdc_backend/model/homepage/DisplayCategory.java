package com.app.bdc_backend.model.homepage;

import com.app.bdc_backend.model.product.Category;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "display_categories")
@Getter
@Setter
public class DisplayCategory {

    @Id
    private ObjectId id;

    @DocumentReference
    private Category category;

    private String thumbnailUrl = "https://res.cloudinary.com/daxt0vwoc/image/upload/v1742879507/Screenshot_2025-03-25_121109-removebg-preview_uizizr.png";

}
