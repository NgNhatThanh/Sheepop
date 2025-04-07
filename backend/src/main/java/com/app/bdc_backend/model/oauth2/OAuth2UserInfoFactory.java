package com.app.bdc_backend.model.oauth2;

import com.app.bdc_backend.model.enums.OauthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static Oauth2UserInfo getOAuth2UserInfo(OauthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> new GoogleOauth2UserInfo(attributes);
        };
    }

}