package com.app.bdc_backend.model.enums;

import com.app.bdc_backend.exception.RequestException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OauthProvider {
    GOOGLE("google");

    private final String value;

    OauthProvider(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OauthProvider fromString(String value) {
        for (OauthProvider provider : OauthProvider.values()) {
            if (provider.value.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new RequestException("Invalid provider: " + value);
    }
    public String toString() {
        return value;
    }
}
