package com.app.bdc_backend.facade;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.Notification;
import com.app.bdc_backend.model.dto.response.NotificationDTO;
import com.app.bdc_backend.model.dto.response.OffsetResponse;
import com.app.bdc_backend.model.enums.NotificationScope;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.NotificationService;
import com.app.bdc_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationFacadeService {

    private final NotificationService notificationService;

    private final UserService userService;

    public OffsetResponse<NotificationDTO> getList(NotificationScope scope,
                                  int offset, int limit) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User receiver = userService.findByUsername(username);
        List<Notification> notiList = notificationService.getList(
                receiver,
                scope,
                offset,
                limit);
        List<NotificationDTO> dtoList = notiList.stream().map(notificationService::toDTO).toList();
        return new OffsetResponse<>(dtoList,
                offset + notiList.size());
    }

    public void markAsRead(String notiId){
        Notification notification = notificationService.getById(notiId);
        if(notification == null)
            throw new RequestException("Invalid request: notification not found");
        User user = userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
        if(!notification.getReceiver().equals(user))
            throw new RequestException("Invalid request: receiver doesn't match");
        notification.setRead(true);
        notificationService.save(notification);
    }

    public void markAllAsRead(){
        User user = userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
        List<Notification> notifications = notificationService.getAllUnreadByReceiver(user);
        for(Notification notification : notifications){
            notification.setRead(true);
        }
        notificationService.saveAll(notifications);
    }

    public int countUnread(){
        User user = userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
        return notificationService.countUnread(user);
    }

}
