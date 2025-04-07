package com.app.bdc_backend.controller;

import com.app.bdc_backend.facade.ChatFacadeService;
import com.app.bdc_backend.model.dto.request.SendMessageDTO;
import com.app.bdc_backend.model.dto.response.ChatroomDTO;
import com.app.bdc_backend.config.MyUserDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatFacadeService chatFacadeService;

    @PostMapping("/create_chatroom")
    public ResponseEntity<?> createChatroom(@RequestParam String userId){
        ChatroomDTO room = chatFacadeService.createChatroom(userId);
        return ResponseEntity.ok().body(room);
    }

    @GetMapping("/get_chatroom")
    public ResponseEntity<?> getChatroom(@RequestParam String userId){
        ChatroomDTO room = chatFacadeService.getChatroom(userId);
        return ResponseEntity.ok().body(room);
    }

    @GetMapping("/get_chatroom_list")
    public ResponseEntity<?> getChatroomList(@RequestParam int offset,
                                             @RequestParam int limit){
        return ResponseEntity.ok(chatFacadeService.getChatroomList(offset, limit));
    }

    @GetMapping("/get_messages")
    public ResponseEntity<?> getMessages(@RequestParam String chatroomId,
                                         @RequestParam int offset,
                                         @RequestParam int limit){
        return ResponseEntity.ok(chatFacadeService.getMessages(chatroomId, offset, limit));
    }

    @GetMapping("/get_unread_rooms")
    public ResponseEntity<?> getUnreadRooms(){
        return ResponseEntity.ok(chatFacadeService.getUnreadRooms());
    }

    @MessageMapping("/send_message")
    public void sendMessage(@Payload @Valid SendMessageDTO dto, Authentication auth){
        if(auth != null){
            MyUserDetail principal = (MyUserDetail) auth.getPrincipal();
            String username = principal.getUsername();
            chatFacadeService.sendMessage(dto, username);
        }
        else{

        }

    }
}
