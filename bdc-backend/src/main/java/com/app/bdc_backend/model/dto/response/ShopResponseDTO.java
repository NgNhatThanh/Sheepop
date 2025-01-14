package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ShopResponseDTO {

    private String shopId;

    private String name;

    private String description;

    private String avatarUrl;

    private Date createdAt;

    private int followerCount;

    private int productCount;

    private int reviewCount;

    private float averageRating;

}