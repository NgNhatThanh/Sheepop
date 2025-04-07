package com.app.bdc_backend.util;

import java.util.Random;

public class RandomUtil {

    private static final Random rand = new Random();

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String LOWER = UPPER.toLowerCase();

    private static final String DIGIT = "0123456789";

    private static final String ALPHABET_NUMERIC = UPPER + LOWER + DIGIT;

    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET_NUMERIC.charAt(rand.nextInt(ALPHABET_NUMERIC.length())));
        }
        return sb.toString();
    }

}
