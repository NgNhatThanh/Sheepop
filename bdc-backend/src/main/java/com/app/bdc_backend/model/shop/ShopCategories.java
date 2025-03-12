package com.app.bdc_backend.model.shop;

import com.app.bdc_backend.model.product.Category;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "shop_categories")
@Getter
@Setter
public class ShopCategories {

    @Id
    private ObjectId id;

    private String shopId;

    @DocumentReference(lazy = true)
    private List<Category> categories = new ArrayList<>();

}
