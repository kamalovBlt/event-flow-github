package com.technokratos.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class VerifyCodeGenerator {

    private static final Random RANDOM = new Random();

    public String generateVerifyCode() {
        int code = 1000 + RANDOM.nextInt(9000);
        return String.valueOf(code);
    }
}

