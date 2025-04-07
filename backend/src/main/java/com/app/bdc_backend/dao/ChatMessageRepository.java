package com.app.bdc_backend.dao;

import com.app.bdc_backend.model.ChatMessage;
import com.app.bdc_backend.model.ChatRoom;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    ChatMessage findTopByRoomOrderByCreatedAtDesc(ChatRoom room);

    Window<ChatMessage> findLastByRoomOrderByCreatedAtDesc(ChatRoom room,
                                                           OffsetScrollPosition offfset,
                                                           Limit limit);
    
}
