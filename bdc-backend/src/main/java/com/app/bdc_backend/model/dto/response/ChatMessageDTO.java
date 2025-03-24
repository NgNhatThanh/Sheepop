package com.app.bdc_backend.model.dto.response;

import com.app.bdc_backend.model.enums.MessageType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ChatMessageDTO {

    private String id;

    private String chatroomId;

    private MessageType type;

    private String senderUsername;

    private String content;

    private Date createdAt;

}
