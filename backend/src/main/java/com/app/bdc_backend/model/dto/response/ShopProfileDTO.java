package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ShopProfileDTO {

    private String id;

    private String username;

    private String shopName;

    private String description;

    private String avatarUrl;

    private String email;

    private String phoneNumber;

    private Date createdAt;

}
