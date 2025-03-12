package com.app.bdc_backend.model.product;

import com.app.bdc_backend.model.enums.RestrictStatus;
import com.app.bdc_backend.model.shop.Shop;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@Document(collection = "products")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @EqualsAndHashCode.Include
    private ObjectId id;

    private String name;

    @DocumentReference(lazy = true)
    private Shop shop;

    private String description;

    private String thumbnailUrl;

    private long price;

    private int quantity;

    @DocumentReference(lazy = true)
    private Category category;

    private int weight;

    @DocumentReference(lazy = true)
    private List<ProductSKU> skuList;

    @DocumentReference(lazy = true)
    private List<ProductMedia> mediaList;

    private long revenue;

    private int sold;

    private double averageRating;

    private int totalReviews;

    private String location;

    private boolean visible;

    private Date createdAt;

    private Date updatedAt;

    private boolean restricted;

    private String restrictedReason;

    private RestrictStatus restrictStatus;

    private boolean deleted = false;

}
