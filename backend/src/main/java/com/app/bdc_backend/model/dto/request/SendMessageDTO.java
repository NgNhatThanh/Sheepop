package com.app.bdc_backend.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageDTO {

    @NotNull(message = "roomId mustn't be null")
    private String chatroomId;

    @NotNull(message = "content mustn't be null")
    private String content;

    @NotNull(message = "type mustn't be null")
    private String type;

}
