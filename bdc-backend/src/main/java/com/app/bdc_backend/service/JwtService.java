package com.app.bdc_backend.service;

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

    @Value("${JWT_SECRET_KEY}")
    private String jwtSecret;

    @Value("${ACCESS_TOKEN_EXPIRATION}")
    private long accessTokenExpiration;

    @Value("${REFRESH_TOKEN_EXPIRATION}")
    @Getter
    private long refreshTokenExpiration;

    private final UserDetailsService userDetailsService;

    private Random rand = new Random();

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

    public String generateAccessToken(String username, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(accessTokenExpiration);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(getSignKey())
                .compact();
    }

    public String generateRefreshToken(String username, Map<String, Object> claims) {
        Instant expiration = Instant.now().plusSeconds(refreshTokenExpiration);
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
        byte[] keyBytes = Decoders.BASE64URL.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
