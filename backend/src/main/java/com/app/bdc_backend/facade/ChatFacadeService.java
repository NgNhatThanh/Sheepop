package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.ChatMessage;
import com.app.bdc_backend.model.ChatRoom;
import com.app.bdc_backend.model.dto.request.SendMessageDTO;
import com.app.bdc_backend.model.dto.response.ChatMessageDTO;
import com.app.bdc_backend.model.dto.response.ChatroomDTO;
import com.app.bdc_backend.model.dto.response.OffsetResponse;
import com.app.bdc_backend.model.enums.MessageType;
import com.app.bdc_backend.model.shop.Shop;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.ChatService;
import com.app.bdc_backend.service.user.ShopService;
import com.app.bdc_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatFacadeService {

    private final ChatService chatService;

    private final UserService userService;

    private final ShopService shopService;

    public ChatroomDTO createChatroom(String userId){
        List<User> participants = getParticipants(userId);
        ChatRoom chatRoom = chatService.createChatroom(participants);
        return toChatroomDTO(chatRoom);
    }

    public ChatroomDTO getChatroom(String userId){
        List<User> participants = getParticipants(userId);
        ChatRoom chatRoom = chatService.getByParticipants(participants);
        if(chatRoom == null)
            chatRoom = chatService.createChatroom(participants);
        else{
            ChatMessage lastMsg = chatService.getLastMessageByRoom(chatRoom);
            lastMsg.setRead(true);
            chatService.saveMessage(lastMsg);
        }
        return toChatroomDTO(chatRoom);
    }

    private List<User> getParticipants(String userId){
        User receiver = userService.findById(userId);
        if(receiver == null)
            throw new RequestException("Invalid request: user not found");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return List.of(user, receiver);
    }

    public OffsetResponse<ChatroomDTO> getChatroomList(int offset, int limit) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userService.findByUsername(username);
        List<ChatRoom> roomList =  chatService.getChatroomList(sender, offset, limit);
        List<ChatroomDTO> dtoList = roomList.stream().map(this::toChatroomDTO).toList();
        return new OffsetResponse<>(
                dtoList, offset + dtoList.size()
        );
    }

    public void sendMessage(SendMessageDTO dto, String senderUsername) {
        MessageType type = MessageType.fromString(dto.getType());
        ChatRoom room = chatService.getChatroomById(dto.getChatroomId());
        if(room == null)
            throw new RequestException("Invalid request: room not found");
        User sender = userService.findByUsername(senderUsername);
        if(!chatService.checkInRoom(room, sender))
            throw new RequestException("Invalid request: you're not participating in the chatroom");
        ChatMessage message = new ChatMessage();
        message.setRoom(room);
        message.setType(type);
        message.setContent(dto.getContent());
        message.setSender(sender);
        chatService.sendMessage(message, senderUsername);
    }

    private ChatroomDTO toChatroomDTO(ChatRoom room){
        String curUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User receiver = curUsername.equals(room.getParticipants().get(0).getUsername()) ?
                room.getParticipants().get(1) : room.getParticipants().get(0);
        Shop shop = shopService.findByUser(receiver);
        ChatroomDTO dto = new ChatroomDTO();
        ChatMessage lastMsg = chatService.getLastMessageByRoom(room);
        if(lastMsg != null){
            dto.setLastMessage(chatService.toChatMessageDTO(lastMsg));
            dto.setRead(!lastMsg.getSender().equals(receiver) || lastMsg.isRead());
        }
        dto.setId(room.getId().toString());
        dto.getReceiver().setUsername(receiver.getUsername());
        dto.getReceiver().setThumbnailUrl(shop.getAvatarUrl());
        dto.getReceiver().setShopName(shop.getName());
        dto.getReceiver().setId(receiver.getId().toString());
        return dto;
    }

    public OffsetResponse<ChatMessageDTO> getMessages(String chatroomId, int offset, int limit) {
        ChatRoom room = chatService.getChatroomById(chatroomId);
        if(room == null)
            throw new RequestException("Invalid request: room not found");
        User sender = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!chatService.checkInRoom(room, sender))
            throw new RequestException("Invalid request: you're not participating in the chatroom");
        List<ChatMessageDTO> dtos = chatService.getMessages(room, offset, limit);
        return new OffsetResponse<>(dtos, offset + dtos.size());
    }

    public List<ChatroomDTO> getUnreadRooms() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userService.findByUsername(username);
        List<ChatRoom> allRooms = chatService.getChatroomList(sender, 0, Integer.MAX_VALUE);
        return allRooms.stream().map(room -> {
            ChatroomDTO dto = new ChatroomDTO();
            dto.setId(room.getId().toString());
            ChatMessage lastMsg = chatService.getLastMessageByRoom(room);
            dto.setRead(lastMsg == null || lastMsg.getSender().equals(sender) || lastMsg.isRead());
            return dto;
        }).filter(dto -> !dto.isRead()).toList();
    }
}