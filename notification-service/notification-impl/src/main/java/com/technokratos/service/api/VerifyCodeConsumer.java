package com.technokratos.service.api;

import com.technokratos.dto.VerifyCodeDTO;

public interface VerifyCodeConsumer {
    void receiveCode(VerifyCodeDTO verifyCode);
}
