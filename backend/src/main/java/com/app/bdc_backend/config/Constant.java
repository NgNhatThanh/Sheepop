package com.app.bdc_backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class Constant {

    @Value("${JWT_SECRET_KEY}")
    private String jwtSecret;

    @Value("${ACCESS_TOKEN_EXPIRATION}")
    private long accessTokenExpiration;

    @Value("${REFRESH_TOKEN_EXPIRATION}")
    private long refreshTokenExpiration;

    @Value("${server.servlet.session.cookie.domain}")
    private String domain;

    @Value("${fe_base_url}")
    private String feBaseUrl;

    private final String placeOrderRedirectUrl = "/myshop/order-list?type=1";

}
