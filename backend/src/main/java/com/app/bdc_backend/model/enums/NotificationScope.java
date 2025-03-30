package com.app.bdc_backend.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum NotificationScope {
    SHOP("shop"), BUYER("buyer");

    private final String value;

    NotificationScope(String value) {
        this.value = value;
    }

    public String toString(){
        return value;
    }

    @JsonCreator
    public static NotificationScope fromString(String value) {
        for (NotificationScope scope : NotificationScope.values()) {
            if (scope.value.equalsIgnoreCase(value)) {
                return scope;
            }
        }
        throw new IllegalArgumentException("Invalid scope: " + value);
    }

}