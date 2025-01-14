package com.app.bdc_backend.controller;

import com.app.bdc_backend.model.dto.response.UserResponseDTO;
import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.service.UserService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        String curUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!curUsername.equals(username)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "You are not the current user"
            ));
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "User not found"
            ));
        }
        UserResponseDTO response = ModelMapper.getInstance().map(user, UserResponseDTO.class);
        response.setId(user.getId().toString());
        return ResponseEntity.ok(response);
    }

}
