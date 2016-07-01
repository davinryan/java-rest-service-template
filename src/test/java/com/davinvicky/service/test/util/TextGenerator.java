package com.davinvicky.service.test.util;

import java.util.Random;

/**
 * Created by ryanda on 09/05/2016.
 */
public class TextGenerator {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int N = ALPHABET.length();
    private static final Random RANDOM = new Random();

    public static String generateAlphaText(boolean uppercase, int length) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(N)));
        }
        String result = sb.toString();
        if (uppercase) {
            return result.toUpperCase();
        }
        return result;
    }

}
