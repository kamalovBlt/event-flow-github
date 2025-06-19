package com.technokratos.service.impl;

import com.technokratos.dto.VerifyCodeDTO;
import com.technokratos.service.api.VerifyCodeConsumer;
import com.technokratos.service.api.VerifyCodeSender;
import com.technokratos.util.RabbitVariables;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitVerifyCodeConsumer implements VerifyCodeConsumer {

    private final VerifyCodeSender verifyCodeSender;

    @Override
    @RabbitListener(queues = {RabbitVariables.EMAIL_CODE_QUEUE_NAME})
    public void receiveCode(VerifyCodeDTO verifyCode) {
        verifyCodeSender.sendVerifyCode(verifyCode);
    }

}
