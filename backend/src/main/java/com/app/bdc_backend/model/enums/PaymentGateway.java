package com.app.bdc_backend.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentGateway {
    VNPAY("vnpay");

    private final String value;

    PaymentGateway(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentGateway fromString(String value) {
        for (PaymentGateway type : PaymentGateway.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid payment gateway: " + value);
    }
}
