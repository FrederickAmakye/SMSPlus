package com.frederickamakye.smsplus.utils;

import java.security.SecureRandom;

public class StudentIdGenerator {

    private static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generate() {

        char[] id = new char[16];

        for (int i = 0; i < id.length; i++) {
            int index = random.nextInt(ALPHA_NUMERIC.length());
            id[i] = ALPHA_NUMERIC.charAt(index);
        }

        return new String(id);
    }
}