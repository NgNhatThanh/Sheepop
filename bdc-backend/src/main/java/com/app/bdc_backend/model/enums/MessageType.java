package com.app.bdc_backend.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MessageType {
    TEXT("text"), MEDIA("media");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String toString(){
        return value;
    }

    @JsonCreator
    public static MessageType fromString(String value) {
        for (MessageType type : MessageType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid type: " + value);
    }
}
