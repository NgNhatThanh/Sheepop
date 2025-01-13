package com.app.bdc_backend.controller;

import com.app.bdc_backend.model.User;
import com.app.bdc_backend.model.dto.LoginDTO;
import com.app.bdc_backend.model.dto.LogoutDTO;
import com.app.bdc_backend.model.dto.RegistrationDTO;
import com.app.bdc_backend.service.JwtService;
import com.app.bdc_backend.service.UserService;
import com.app.bdc_backend.service.redis.JwtRedisService;
import com.app.bdc_backend.util.Mapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private UserService userSevice;

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;

    private JwtRedisService jwtRedisService;
    
    private final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDTO dto){
        User newUser = Mapper.getInstance().map(dto, User.class);
        try{
            userSevice.register(newUser);
            String accessToken = jwtService.generateAccessToken(newUser.getUsername(), new HashMap<>());
            String refreshToken = jwtService.generateRefreshToken(newUser.getUsername(), new HashMap<>());
            jwtRedisService.setNewRefreshToken(newUser.getUsername(), refreshToken);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(refreshToken).toString())
                    .body(Map.of(
                    "token", accessToken
            ));
        }
        catch(RuntimeException e){
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto){
        try{
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
            authenticationManager.authenticate(authToken);
            String accessToken = jwtService.generateAccessToken(dto.getUsername(), new HashMap<>());
            String refreshToken = jwtService.generateRefreshToken(dto.getUsername(), new HashMap<>());
            jwtRedisService.setNewRefreshToken(dto.getUsername(), refreshToken);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(refreshToken).toString())
                    .body(Map.of(
                            "token", accessToken
                    ));
        }
        catch (AuthenticationException e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid username or password"
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutDTO dto){
        jwtRedisService.deleteRefreshToken(dto.getUsername());
        ResponseCookie cookie = ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, defaultValue = "") String token){
        if (token == null || token.isEmpty()
                || !jwtService.isTokenValid(token)
                || !jwtRedisService.isRefreshTokenValid(jwtService.extractUsername(token), token))
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid refresh token"
            ));
        String username = jwtService.extractUsername(token);
        String accessToken = jwtService.generateAccessToken(
                username,
                new HashMap<>());
        String refreshToken = jwtService.generateRefreshToken(
                username,
                new HashMap<>()
        );
        jwtRedisService.setNewRefreshToken(username, refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(refreshToken).toString())
                .body(Map.of(
                        "token", accessToken
                ));
    }

    private ResponseCookie getRefreshTokenCookie(String token){
        return ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtService.getRefreshTokenExpiration())
                .build();
    }

}
