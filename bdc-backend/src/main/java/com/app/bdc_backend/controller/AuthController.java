package com.app.bdc_backend.controller;

import com.app.bdc_backend.config.Constant;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.facade.AuthFacadeService;
import com.app.bdc_backend.model.dto.AuthResponseDTO;
import com.app.bdc_backend.model.dto.request.LoginDTO;
import com.app.bdc_backend.model.dto.request.RegistrationDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController{

    private final AuthFacadeService authFacadeService;

    private final AuthenticationManager authenticationManager;

    private final Constant constant;

    private final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDTO dto){
        AuthResponseDTO res = authFacadeService.registerUser(dto);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(res.getRefreshToken()).toString())
                .body(Map.of(
                        "token", res.getAccessToken()
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try{
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
            authenticationManager.authenticate(authToken);
        }
        catch (Exception e){
            throw new RequestException("Invalid username or password");
        }
        AuthResponseDTO res = authFacadeService.login(dto);
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("token", res.getAccessToken());
        if(res.isAdmin()) resMap.put("admin", true);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(res.getRefreshToken()).toString())
                .body(resMap);
    }

    @PostMapping("/oauth2/login")
    public ResponseEntity<?> oauth2Login(@RequestParam(value = "provider") String provider,
                                         @RequestParam(value = "code") String code){
        provider = provider.toLowerCase();
        if (provider.equals("google")) {
            AuthResponseDTO res = authFacadeService.oauthLogin(code, provider);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(res.getRefreshToken()).toString())
                    .body(Map.of(
                            "token", res.getAccessToken()
                    ));
        }
        else{
            throw new RequestException("Invalid provider");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        String accessToken = request.getHeader("Authorization").substring(7);
        authFacadeService.logout(accessToken);
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

    @GetMapping("/ping")
    public ResponseEntity<?> ping(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<Map<String, String>> refreshToken(
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, defaultValue = "") String token){
        AuthResponseDTO res = authFacadeService.refresh(token);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(res.getRefreshToken()).toString())
                .body(Map.of(
                        "token", res.getAccessToken()
                ));
    }

    private ResponseCookie getRefreshTokenCookie(String token){
        return ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(true)
                .domain(constant.getDomain())
                .path("/")
                .maxAge(constant.getRefreshTokenExpiration())
                .build();
    }

}
