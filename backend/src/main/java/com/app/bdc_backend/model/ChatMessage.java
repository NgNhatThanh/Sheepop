package com.app.bdc_backend.model;

import com.app.bdc_backend.model.enums.MessageType;
import com.app.bdc_backend.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;

@Document(collection = "chat_messages")
@Getter
@Setter
public class ChatMessage {

    @Id
    private ObjectId id;

    @DocumentReference(lazy = true)
    private ChatRoom room;

    private User sender;

    private MessageType type;

    private String content;

    private boolean read;

    private Date createdAt = new Date();

}