package com.technokratos.service.api;

public interface VerifyCodeSender {
    void send(String code, String email);
}
