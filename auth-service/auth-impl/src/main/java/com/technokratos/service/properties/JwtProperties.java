package com.technokratos.service.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {

    private final String privateSecretKey;

    private final String publicSecretKey;

    /**Время действия токена доступа.
     * В properties - в минутах.
     * В коде - в миллисекундах.
     * */
    private final long accessTokenValidity;

    /**Время действия токена обновления.
     * В properties - в минутах.
     * В коде - в миллисекундах.
     * */
    private final long refreshTokenValidity;

    /**Время действия кода подтверждения.
     * В properties - в минутах.
     * В коде - в миллисекундах.
     * */
    private final long verifyCodeValidity;

    public JwtProperties(
            @Value("${jwt.private-secret-key}") String privateSecretKey,
            @Value("${jwt.public-secret-key}") String publicSecretKey,
            @Value("${jwt.access-token-validity}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidity,
            @Value("${jwt.verify-code-validity}") long verifyCodeValidity
    ) {
        this.privateSecretKey = privateSecretKey;
        this.publicSecretKey = publicSecretKey;
        this.accessTokenValidity = accessTokenValidity * 60 * 1000;
        this.refreshTokenValidity = refreshTokenValidity * 60 * 1000;
        this.verifyCodeValidity = verifyCodeValidity * 60 * 1000;
    }
}
