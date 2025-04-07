package com.app.bdc_backend.model;

import com.app.bdc_backend.model.enums.NotificationScope;
import com.app.bdc_backend.model.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;

@Document(collection = "notifications")
@Getter
@Setter
@Builder
public class Notification {

    @Id
    private ObjectId id;

    private String content;

    private NotificationScope scope;

    private int itemCount;

    @Builder.Default
    private String thumbnailUrl = "https://res.cloudinary.com/daxt0vwoc/image/upload/v1742230894/png-transparent-red-bell-notification-thumbnail_mvxxqa.png";

    @DocumentReference(lazy = true)
    private User receiver;

    private String redirectUrl;

    private boolean read;

    @Builder.Default
    private Date createdAt = new Date();

}
