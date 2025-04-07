package com.app.bdc_backend.model.shop;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "follows")
@Getter
@Setter
public class Follow {

    @Id
    private ObjectId id;

    private String shopId;

    private String userId;

    public Follow(String shopId, String userId) {
        this.shopId = shopId;
        this.userId = userId;
    }

}
