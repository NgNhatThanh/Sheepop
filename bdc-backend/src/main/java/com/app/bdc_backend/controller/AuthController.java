package com.app.bdc_backend.controller;

import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.model.dto.LoginDTO;
import com.app.bdc_backend.model.dto.LogoutDTO;
import com.app.bdc_backend.model.dto.RegistrationDTO;
import com.app.bdc_backend.service.JwtService;
import com.app.bdc_backend.service.Oauth2Service;
import com.app.bdc_backend.service.UserService;
import com.app.bdc_backend.service.redis.JwtRedisService;
import com.app.bdc_backend.util.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController{

    private final UserService userSevice;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final JwtRedisService jwtRedisService;

    private final Oauth2Service oauth2Service;
    
    private final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDTO dto){
        User newUser = ModelMapper.getInstance().map(dto, User.class);
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

    @GetMapping("/oauth2")
    public ResponseEntity<?> oauth2Login(@RequestParam(value = "provider") String provider,
                                         @RequestParam(value = "code") String code){
        provider = provider.toLowerCase();
        if (provider.equals("google")) {
            try{
                Map<String, Object> getUserInfo = oauth2Service.getOauth2Profile(code, provider);
                String fullName = (String) getUserInfo.get("name");
                String email = (String) getUserInfo.get("email");
                String username = email.split("@")[0];
                User user = userSevice.findByUsername(username);
                if(user == null){
                    return register(RegistrationDTO.builder()
                            .username(username)
                            .email(email)
                            .fullName(fullName)
                            .password("")
                            .build());
                }
                else{
                    return login(new LoginDTO(user.getUsername(), ""));
                }
            }
            catch (Exception e){
                return ResponseEntity.badRequest().body(
                        Map.of("message", e.getMessage())
                );
            }
        }
        else{
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid provider"
            ));
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

    @GetMapping("/refreshToken")
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
