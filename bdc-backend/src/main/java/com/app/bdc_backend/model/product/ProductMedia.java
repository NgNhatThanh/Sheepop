package com.app.bdc_backend.model.product;

import com.app.bdc_backend.model.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_media")
@Getter
@Setter
public class ProductMedia {

    @Id
    private ObjectId id;

    private String url;

    private MediaType type;

}
