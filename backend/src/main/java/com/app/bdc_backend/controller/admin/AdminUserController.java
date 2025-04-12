package com.app.bdc_backend.controller.admin;

import com.app.bdc_backend.config.SwaggerSecurityName;
import com.app.bdc_backend.facade.admin.AdminUserFacadeService;
import com.app.bdc_backend.model.dto.request.RestrictUserDTO;
import com.app.bdc_backend.model.dto.response.PageResponse;
import com.app.bdc_backend.model.dto.response.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/user")
@SecurityRequirement(name = SwaggerSecurityName.JWT_AUTH)
public class AdminUserController {

    private final AdminUserFacadeService adminUserFacadeService;

    @GetMapping("/get_list")
    @Operation(
            summary = "Get list of all users with filters and sorting"
    )
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
    @Operation(
            summary = "Get detail information of an user by Id"
    )
    public ResponseEntity<?> getUser(@PathVariable String userId){
        return ResponseEntity.ok(adminUserFacadeService.getUserProfile(userId));
    }

    @PostMapping("/delete_user")
    @Operation(
            summary = "Delete an user"
    )
    public ResponseEntity<?> deleteUser(@RequestBody @Valid RestrictUserDTO dto){
        adminUserFacadeService.deleteUser(dto.getUserId(), dto.getReason());
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }

    @PostMapping("/restore_user/{userId}")
    @Operation(
            summary = "Restore an user"
    )
    public ResponseEntity<?> restoreUser(@PathVariable String userId){
        adminUserFacadeService.restoreUser(userId);
        return ResponseEntity.ok().body(Map.of("status", "success"));
    }
}
