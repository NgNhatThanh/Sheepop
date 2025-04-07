package com.app.bdc_backend.dao;

import com.app.bdc_backend.model.ChatRoom;
import com.app.bdc_backend.model.user.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

    ChatRoom findChatRoomByParticipants(List<User> participants);

    Window<ChatRoom> findByParticipantsContainingOrderByLastActiveDesc(User user,
                                                                       OffsetScrollPosition offsetScrollPosition,
                                                                       Limit limit);
    
}
