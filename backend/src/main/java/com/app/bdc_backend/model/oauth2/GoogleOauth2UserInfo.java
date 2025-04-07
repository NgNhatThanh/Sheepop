package com.app.bdc_backend.model.oauth2;

import java.util.Map;

public class GoogleOauth2UserInfo extends Oauth2UserInfo {

    public GoogleOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getUsername() {
        return attributes.get("email").toString().split("@")[0];
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getFullName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getAvatarUrl() {
        return attributes.get("picture").toString();
    }
}
