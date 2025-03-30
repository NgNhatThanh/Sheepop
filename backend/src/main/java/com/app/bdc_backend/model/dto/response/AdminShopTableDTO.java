package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AdminShopTableDTO {

    private String id;

    private String name;

    private Owner owner;

    private String description;

    private String avatarUrl;

    private Date createdAt;

    private int productCount;

    private long revenue;

    private double averageRating;

    private boolean deleted;

    private String deleteReason;

    @Getter
    @Setter
    public static class Owner{

        private String id;

        private String fullName;

    }

}
