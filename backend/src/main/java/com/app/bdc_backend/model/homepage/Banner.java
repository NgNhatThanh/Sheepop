package com.app.bdc_backend.model.homepage;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "banner")
@Getter
@Setter
public class Banner {

    @Id
    private ObjectId id;

    private String imageUrl;

    private String redirectUrl;

}
