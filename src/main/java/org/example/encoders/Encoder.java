package org.example.encoders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Encoder {

    private Encoder() {
    }

    public static String encodeToBase64(String email, String code) {
        String toEncode = email + ":" + code;
        return Base64.getEncoder().encodeToString(toEncode.getBytes(StandardCharsets.UTF_8));
    }
}
