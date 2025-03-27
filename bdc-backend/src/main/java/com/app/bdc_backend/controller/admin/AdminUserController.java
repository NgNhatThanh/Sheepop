package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.facade.admin.AdminUserFacadeService;
import com.app.bdc_backend.model.dto.request.RestrictUserDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.UserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/user")
public class AdminUserController {

    private final AdminUserFacadeService adminUserFacadeService;

    @GetMapping("/get_list")
    public ResponseEntity<?> getUserList(@RequestParam(value = "type") int type,
                                         @RequestParam(value = "filterType", defaultValue = "0") int filterType,
                                         @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                         @RequestParam int sortType,
                                         @RequestParam(value = "page") int page,
                                         @RequestParam (value = "limit") int limit){
        Page<UserResponseDTO> pageRes = adminUserFacadeService.getUserList(
                type,
                filterType,
                keyword,
                sortType,
                page,
                limit
        );
        PageResponse<UserResponseDTO> response = new PageResponse<>(pageRes);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId){
        return ResponseEntity.ok(adminUserFacadeService.getUserProfile(userId));
    }

    @PostMapping("/delete_user")
    public ResponseEntity<?> deleteUser(@RequestBody @Valid RestrictUserDTO dto){ // include userId, and reason
        adminUserFacadeService.deleteUser(dto.getUserId(), dto.getReason());
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }

    @PostMapping("/restore_user/{userId}")
    public ResponseEntity<?> restoreUser(@PathVariable String userId){
        adminUserFacadeService.restoreUser(userId);
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }
}
