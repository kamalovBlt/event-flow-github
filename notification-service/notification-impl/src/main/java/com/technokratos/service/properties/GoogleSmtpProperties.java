package com.technokratos.service.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class GoogleSmtpProperties {

    private final String fromEmail;

    public GoogleSmtpProperties(@Value("${spring.mail.username}") String fromEmail) {
        this.fromEmail = fromEmail;
    }

}
