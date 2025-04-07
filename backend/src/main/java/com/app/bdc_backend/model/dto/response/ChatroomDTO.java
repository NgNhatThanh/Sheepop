package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatroomDTO {

    private String id;

    private Receiver receiver = new Receiver();

    private ChatMessageDTO lastMessage;

    private boolean read = true;

    @Getter
    @Setter
    public static class Receiver{

        private String id;

        private String username;

        private String shopName;

        private String thumbnailUrl;

    }

}
