package com.app.bdc_backend.controller.common;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.facade.NotificationFacadeService;
import com.app.bdc_backend.model.dto.response.NotificationDTO;
import com.app.bdc_backend.model.dto.response.OffsetResponse;
import com.app.bdc_backend.model.enums.NotificationScope;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class NotificationController {

    private final NotificationFacadeService notificationFacadeService;

    @GetMapping("/get_list")
    @Operation(summary = "Get list of notifications")
    public ResponseEntity<OffsetResponse<NotificationDTO>> getNotificationList(@RequestParam(value = "scope") String scope,
                                                                             @RequestParam(value = "offset") int offset,
                                                                             @RequestParam(value = "limit") int limit) {
        NotificationScope scp;
        try {
            scp = NotificationScope.fromString(scope);
        } catch (IllegalArgumentException e) {
            throw new RequestException(e.getMessage());
        }
        return ResponseEntity.ok(notificationFacadeService.getList(scp, offset, limit));
    }

    @GetMapping("/count_unread")
    @Operation(summary = "Count unread notifications")
    public ResponseEntity<Integer> countUnread() {
        return ResponseEntity.ok(notificationFacadeService.countUnread());
    }

    @PostMapping("/mark_as_read")
    @Operation(summary = "Mark notifications as read")
    public ResponseEntity<Map<String, String>> markAsRead(@RequestParam(required = false) String notiId) {
        if(notiId == null) {
            notificationFacadeService.markAllAsRead();
        } else {
            notificationFacadeService.markAsRead(notiId);
        }
        return ResponseEntity.ok(Map.of("status", "success"));
    }
}
