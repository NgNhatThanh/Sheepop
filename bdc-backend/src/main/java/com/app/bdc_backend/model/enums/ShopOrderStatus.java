package com.app.bdc_backend.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShopOrderStatus {

    public static int PENDING = 1;

    public static int PREPARING = 2;

    public static int SENT = 3;

    public static int DELIVERING = 4;

    public static int COMPLETED = 5;

    public static int RATED = 6;

    public static int CANCELLED = 7;

    public static List<Integer> getAllStatuses() {
        return Arrays.stream(ShopOrderStatus.class.getDeclaredFields())
                .filter(field -> field.getType() == int.class)
                .map(field -> {
                    try {
                        return field.getInt(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
