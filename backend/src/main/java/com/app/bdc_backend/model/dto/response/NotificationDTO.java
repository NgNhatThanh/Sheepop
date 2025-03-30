package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.enums.NotificationScope;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class NotificationDTO {

    private String id;

    private String content;

    private int itemCount;

    private String redirectUrl;

    private String thumbnailUrl;

    private NotificationScope scope;

    private boolean read;

    private Date createdAt;

}
