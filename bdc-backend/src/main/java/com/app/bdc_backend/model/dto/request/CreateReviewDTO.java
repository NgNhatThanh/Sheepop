package com.app.bdc_backend.model.dto.request;

import com.app.bdc_backend.model.product.ProductReviewMedia;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateReviewDTO {

    private String shopOrderId;

    private List<ItemReview> itemReviews;

    @Getter
    @Setter
    public static class ItemReview {

        private String orderItemId;

        private int rating;

        private String content;

        private List<ProductReviewMedia> mediaList;

    }

}
