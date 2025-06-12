package com.technokratos.service.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class GoogleOAuthProperties {

    private final String clientId;
    private final String clientSecret;
    private final List<String> scopes;
    private final String redirectUri;

    public GoogleOAuthProperties(
            @Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId,
            @Value("${spring.security.oauth2.client.registration.google.client-secret}") String clientSecret,
            @Value("${spring.security.oauth2.client.registration.google.scope}") List<String> scopes,
            @Value("${spring.security.oauth2.client.registration.google.redirect-uri}") String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scopes = scopes;
        this.redirectUri = redirectUri;
    }
}
