package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.product.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ShopInfoDTO {

    private String id;

    private String userId;

    private String name;

    private String description;

    private String avatarUrl;

    private Date createdAt;

    private boolean isFollowing;

    private int followerCount;

    private int productCount;

    private int totalReviews;

    private float averageRating;

    private List<Category> shopCategories;

}