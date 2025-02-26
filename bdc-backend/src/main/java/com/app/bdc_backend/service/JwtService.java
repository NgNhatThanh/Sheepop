package com.app.bdc_backend.service;

import com.app.bdc_backend.config.Constant;
import com.app.bdc_backend.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserDetailsService userDetailsService;

    private Random rand = new Random();

    private final Constant constant;

    public String extractUsername(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractId(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getId();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(extractUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String generateAccessToken(User user, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(constant.getAccessTokenExpiration());
        claims.put("avatarUrl", user.getAvatarUrl());
        claims.put("fullName", user.getFullName());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(getSignKey())
                .compact();
    }

    public String generateRefreshToken(String username, Map<String, Object> claims) {
        Instant expiration = Instant.now().plusSeconds(constant.getRefreshTokenExpiration());
        String id = String.valueOf(rand.nextInt(100000000, 999999999));
        return Jwts.builder()
                .setClaims(claims)
                .setId(id)
                .setSubject(username)
                .setExpiration(Date.from(expiration))
                .signWith(getSignKey())
                .compact();
    }

    public boolean isTokenValid(String token){
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return !claims.getExpiration().before(new Date());
        }
        catch (Exception e){
            return false;
        }
    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64URL.decode(constant.getJwtSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
