package com.stove.drm.adapter.biz.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UUIDGen {
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public static String generate() {
        // 현재 시각 (밀리초까지)
        String timePart = LocalDateTime.now().format(formatter);

        // 랜덤 4자리
        StringBuilder randPart = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            randPart.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }

        return timePart + randPart.toString();
    }

    public static void main(String[] args) {
        System.out.println(generate());
    }

}
