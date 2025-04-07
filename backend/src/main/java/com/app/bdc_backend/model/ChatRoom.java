package com.app.bdc_backend.model;

import com.app.bdc_backend.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Document(collection = "chat_rooms")
@Getter
@Setter
public class ChatRoom {

    @Id
    private ObjectId id;

    @DocumentReference
    private List<User> participants;

    private Date lastActive = new Date();

    public void setParticipants(List<User> participants) {
        if(participants.get(0).getId().compareTo(participants.get(participants.size() - 1).getId()) > 0)
            participants = List.of(participants.get(1), participants.get(0));
        this.participants = participants;
    }

}