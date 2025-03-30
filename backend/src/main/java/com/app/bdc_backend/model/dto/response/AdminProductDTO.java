package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.enums.RestrictStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AdminProductDTO {

    private String id;

    private String username;

    private String shopName;

    private String thumbnailUrl;

    private String name;

    private long price;

    private Date createdAt;

    private Date updatedAt;

    private boolean deleted;

    private boolean visible;

    private boolean restricted;

    private String restrictReason;

    private RestrictStatus restrictStatus;

}
