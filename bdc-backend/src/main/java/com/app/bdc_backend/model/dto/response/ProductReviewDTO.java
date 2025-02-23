package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.product.ProductAttribute;
import com.app.bdc_backend.model.product.ProductReviewMedia;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProductReviewDTO {

    private String id;

    private Reviewer reviewer = new Reviewer();

    private Item item = new Item();

    private int rating;

    private String content;

    private int reactionCount;

    private List<ProductReviewMedia> mediaList;

    private Date createdAt;

    @Getter
    @Setter
    public static class Reviewer{

        private String username;

        private String avatarUrl;

    }

    @Getter
    @Setter
    public static class Item{

        private List<ProductAttribute> attributes;

        private int quantity;

    }

}
