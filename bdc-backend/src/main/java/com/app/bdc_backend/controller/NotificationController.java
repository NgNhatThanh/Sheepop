package com.app.bdc_backend.controller;

import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.facade.NotificationFacadeService;
import com.app.bdc_backend.model.enums.NotificationScope;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationFacadeService notificationFacadeService;

    @GetMapping("/get_list")
    public ResponseEntity<?> getNotificationList(@RequestParam String scope,
                                                 @RequestParam int offset,
                                                 @RequestParam int limit) {
        NotificationScope scp;
        try{
            scp = NotificationScope.fromString(scope);
        }
        catch (IllegalArgumentException e){
            throw new RequestException(e.getMessage());
        }
        Map<String, Object> res = new HashMap<>();
        res.put("main", notificationFacadeService.getList(
                scp, offset, limit
        ));
        res.put("unreadCount", notificationFacadeService.countUnread());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/mark_as_read")
    public ResponseEntity<?> markAsRead(@RequestParam(required = false) String notiId){
        if(notiId == null){
            notificationFacadeService.markAllAsRead();
        }
        else{
            notificationFacadeService.markAsRead(notiId);
        }
        return ResponseEntity.ok(Map.of(
                "status", "success"
        ));
    }

}
