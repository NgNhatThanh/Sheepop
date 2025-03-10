package com.app.bdc_backend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateShopProfileDTO {

    private String id;

    private String shopName;

    private String description;

    private String avatarUrl;

}
