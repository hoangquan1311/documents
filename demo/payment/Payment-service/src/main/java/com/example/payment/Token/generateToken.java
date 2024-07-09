package com.example.payment.Token;

import java.util.UUID;

public class generateToken {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
