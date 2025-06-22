package com.app.bdc_backend.controller.common;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.ChatFacadeService;
import com.app.bdc_backend.model.dto.request.SendMessageDTO;
import com.app.bdc_backend.model.dto.response.ChatMessageDTO;
import com.app.bdc_backend.model.dto.response.ChatroomDTO;
import com.app.bdc_backend.model.dto.response.OffsetResponse;
import com.app.bdc_backend.config.MyUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class ChatController {

    private final ChatFacadeService chatFacadeService;

    @PostMapping("/create_chatroom")
    @Operation(summary = "Create a new chatroom")
    public ResponseEntity<ChatroomDTO> createChatroom(@RequestParam(value = "userId") String userId) {
        return ResponseEntity.ok(chatFacadeService.createChatroom(userId));
    }

    @GetMapping("/get_chatroom")
    @Operation(summary = "Get chatroom by user ID")
    public ResponseEntity<ChatroomDTO> getChatroom(@RequestParam(value = "userId") String userId) {
        return ResponseEntity.ok(chatFacadeService.getChatroom(userId));
    }

    @GetMapping("/get_chatroom_list")
    @Operation(summary = "Get list of chatrooms")
    public ResponseEntity<OffsetResponse<ChatroomDTO>> getChatroomList(@RequestParam(value = "offset") int offset,
                                                                      @RequestParam(value = "limit") int limit) {
        return ResponseEntity.ok(chatFacadeService.getChatroomList(offset, limit));
    }

    @GetMapping("/get_messages")
    @Operation(summary = "Get messages in a chatroom")
    public ResponseEntity<OffsetResponse<ChatMessageDTO>> getMessages(@RequestParam(value = "chatroomId") String chatroomId,
                                                                    @RequestParam(value = "offset") int offset,
                                                                    @RequestParam(value = "limit") int limit) {
        return ResponseEntity.ok(chatFacadeService.getMessages(chatroomId, offset, limit));
    }

    @GetMapping("/get_unread_rooms")
    @Operation(summary = "Get unread chatrooms")
    public ResponseEntity<List<ChatroomDTO>> getUnreadRooms() {
        return ResponseEntity.ok(chatFacadeService.getUnreadRooms());
    }

    @MessageMapping("/send_message")
    public void sendMessage(@Payload @Valid SendMessageDTO dto, Authentication auth) {
        if(auth != null) {
            MyUserDetail principal = (MyUserDetail) auth.getPrincipal();
            String username = principal.getUsername();
            chatFacadeService.sendMessage(dto, username);
        }
    }
}
