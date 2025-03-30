package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.NotificationRepository;
import com.app.bdc_backend.model.Notification;
import com.app.bdc_backend.model.dto.response.NotificationDTO;
import com.app.bdc_backend.model.enums.NotificationScope;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationRepository notificationRepository;

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void saveAll(List<Notification> notifications) {
        notificationRepository.saveAll(notifications);
    }

    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
        messagingTemplate.convertAndSendToUser(notification.getReceiver().getUsername(), "/notify",
                toDTO(notification));
    }

    public void sendAllNotis(List<Notification> notiList){
        notificationRepository.saveAll(notiList);
        for(Notification noti : notiList){
            messagingTemplate.convertAndSendToUser(noti.getReceiver().getUsername(), "/notify",
                    toDTO(noti));
        }
    }

    public Notification getById(String id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public List<Notification> getAllUnreadByReceiver(User user) {
        return notificationRepository.findAllByReadAndReceiver(false, user);
    }

    public List<Notification> getList(User receiver,
                                      NotificationScope scope,
                                      int offset, int limit){
        return notificationRepository.findLastByReceiverAndScopeOrderByCreatedAtDesc(
                receiver,
                scope,
                offset > 0 ? ScrollPosition.offset(offset - 1) : ScrollPosition.offset(),
                Limit.of(limit))
                .stream().toList();
    }

    public int countUnread(User receiver){
        return notificationRepository.countByReceiverAndRead(receiver, false);
    }

    public NotificationDTO toDTO(Notification noti) {
        NotificationDTO dto = ModelMapper.getInstance().map(noti, NotificationDTO.class);
        dto.setId(noti.getId().toString());
        return dto;
    }

}
