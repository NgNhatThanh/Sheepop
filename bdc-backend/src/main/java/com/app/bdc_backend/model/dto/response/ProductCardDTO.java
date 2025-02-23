package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCardDTO {

    private String id;

    private String name;

    private String thumbnailUrl;

    private long price;

    private double averageRating;

    private int sold;

}
