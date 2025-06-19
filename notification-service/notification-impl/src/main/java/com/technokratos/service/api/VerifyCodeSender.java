package com.technokratos.service.api;

import com.technokratos.dto.VerifyCodeDTO;

public interface VerifyCodeSender {
    void sendVerifyCode(VerifyCodeDTO verifyCodeDTO);
}
