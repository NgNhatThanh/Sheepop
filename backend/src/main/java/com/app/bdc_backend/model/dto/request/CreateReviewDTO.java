package com.app.bdc_backend.model.dto.request;

import com.app.bdc_backend.model.product.ProductReviewMedia;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateReviewDTO {

    @NotBlank
    private String shopOrderId;

    @NotEmpty
    private List<@Valid ItemReview> itemReviews;

    @Getter
    @Setter
    public static class ItemReview {

        @NotBlank
        private String orderItemId;

        @Min(1)
        @Max(5)
        private int rating;
        
        private String content;

        private List<ProductReviewMedia> mediaList;

    }

}
