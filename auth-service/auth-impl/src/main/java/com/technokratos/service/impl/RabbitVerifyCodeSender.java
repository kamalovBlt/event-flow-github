package com.technokratos.service.impl;

import com.technokratos.dto.VerifyCodeDTO;
import com.technokratos.service.api.VerifyCodeSender;
import com.technokratos.util.RabbitVariables;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitVerifyCodeSender implements VerifyCodeSender {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(String code, String email) {
        rabbitTemplate.convertAndSend(
                RabbitVariables.NOTIFICATION_EXCHANGE_NAME,
                RabbitVariables.EMAIL_CODE_ROUTING_KEY,
                new VerifyCodeDTO(email, code)
        );
    }

}
