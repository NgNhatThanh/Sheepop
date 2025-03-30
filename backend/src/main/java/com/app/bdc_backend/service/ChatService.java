package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.ChatMessageRepository;
import com.app.bdc_backend.dao.ChatRoomRepository;
import com.app.bdc_backend.model.ChatMessage;
import com.app.bdc_backend.model.ChatRoom;
import com.app.bdc_backend.model.dto.response.ChatMessageDTO;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public ChatRoom createChatroom(List<User> users){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setParticipants(users);
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom getByParticipants(List<User> participants){
        return chatRoomRepository.findChatRoomByParticipants(rearrangeUserList(participants));
    }

    public ChatMessage getLastMessageByRoom(ChatRoom room) {
        return chatMessageRepository.findTopByRoomOrderByCreatedAtDesc(room);
    }

    public List<ChatRoom> getChatroomList(User sender, int offset, int limit) {
        return chatRoomRepository.findByParticipantsContainingOrderByLastActiveDesc(
                sender,
                offset > 0 ? ScrollPosition.offset(offset - 1) : ScrollPosition.offset(),
                Limit.of(limit)
        ).toList();
    }

    public ChatRoom getChatroomById(String chatroomId) {
        return chatRoomRepository.findById(chatroomId).orElse(null);
    }

    public boolean checkInRoom(ChatRoom room, User sender){
        return sender.equals(room.getParticipants().get(0))
                || sender.equals(room.getParticipants().get(1));
    }

    public void sendMessage(ChatMessage message, String senderUsername) {
        ChatMessage savedMessage = chatMessageRepository.save(message);
        ChatRoom room = savedMessage.getRoom();
        String receiverUsername = senderUsername.equals(room.getParticipants().get(0).getUsername()) ?
                room.getParticipants().get(1).getUsername() : room.getParticipants().get(0).getUsername();
        ChatMessageDTO dto = toChatMessageDTO(savedMessage);
        messagingTemplate.convertAndSendToUser(receiverUsername, "/chat.queue", dto);
        messagingTemplate.convertAndSendToUser(senderUsername, "/chat.reply", dto);
        room.setLastActive(new Date());
        chatRoomRepository.save(room);
    }

    public List<ChatMessageDTO> getMessages(ChatRoom room, int offset, int limit) {
        Window<ChatMessage> messages = chatMessageRepository.findLastByRoomOrderByCreatedAtDesc(
                room,
                offset > 0 ? ScrollPosition.offset(offset - 1) : ScrollPosition.offset(),
                Limit.of(limit)
        );
        return messages.map(this::toChatMessageDTO).toList();
    }

    public ChatMessageDTO toChatMessageDTO(ChatMessage message){
        ChatMessageDTO dto = ModelMapper.getInstance().map(message, ChatMessageDTO.class);
        dto.setId(message.getId().toString());
        dto.setChatroomId(message.getRoom().getId().toString());
        dto.setSenderUsername(message.getSender().getUsername());
        return dto;
    }

    private List<User> rearrangeUserList(List<User> users){
        if(users.get(0).getId().compareTo(users.get(1).getId()) > 0){
            users = List.of(users.get(1), users.get(0));
        }
        return users;
    }

    public void saveMessage(ChatMessage lastMsg) {
        chatMessageRepository.save(lastMsg);
    }
}
