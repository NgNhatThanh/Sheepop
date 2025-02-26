package com.app.bdc_backend.controller;

import com.app.bdc_backend.config.Constant;
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
        try{
            AuthResponseDTO res = authFacadeService.registerUser(dto);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(res.getRefreshToken()).toString())
                    .body(Map.of(
                            "token", res.getAccessToken()
                    ));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto){
        try{
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
            authenticationManager.authenticate(authToken);
            AuthResponseDTO res = authFacadeService.login(dto);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(res.getRefreshToken()).toString())
                    .body(Map.of(
                            "token", res.getAccessToken()
                    ));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid username or password"
            ));
        }
    }

    @PostMapping("/oauth2/login")
    public ResponseEntity<?> oauth2Login(@RequestParam(value = "provider") String provider,
                                         @RequestParam(value = "code") String code){
        provider = provider.toLowerCase();
        if (provider.equals("google")) {
            try{
                AuthResponseDTO res = authFacadeService.oauthLogin(code, provider);
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(res.getRefreshToken()).toString())
                        .body(Map.of(
                                "token", res.getAccessToken()
                        ));
            }
            catch (Exception e){
                log.info(e.toString());
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        String accessToken = request.getHeader("Authorization").substring(7);
        try{
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
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<Map<String, String>> refreshToken(@CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, defaultValue = "") String token){
        try{
            AuthResponseDTO res = authFacadeService.refresh(token);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(res.getRefreshToken()).toString())
                    .body(Map.of(
                            "token", res.getAccessToken()
                    ));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
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
