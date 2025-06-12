package com.technokratos.service.impl;

import com.technokratos.annotation.Logging;
import com.technokratos.client.GoogleOAuthClient;
import com.technokratos.client.GoogleUserInfoClient;
import com.technokratos.client.dto.GoogleTokenResponse;
import com.technokratos.client.dto.GoogleUserInfo;
import com.technokratos.exception.GoogleAuthException;
import com.technokratos.service.api.GoogleOAuthService;
import com.technokratos.service.properties.GoogleOAuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthServiceImpl implements GoogleOAuthService {

    private final GoogleOAuthClient googleOAuthClient;
    private final GoogleUserInfoClient googleUserInfoClient;
    private final GoogleOAuthProperties googleOAuthProperties;

    @Override
    @Logging
    public GoogleUserInfo getGoogleUserInfo(String code) {

        String requestInfo = "client_id=%s&client_secret=%s&code=%s&grant_type=%s&redirect_uri=%s"
                .formatted(
                        URLEncoder.encode(googleOAuthProperties.getClientId(), StandardCharsets.UTF_8),
                        URLEncoder.encode(googleOAuthProperties.getClientSecret(), StandardCharsets.UTF_8),
                        code,
                        URLEncoder.encode("authorization_code", StandardCharsets.UTF_8),
                        URLEncoder.encode(googleOAuthProperties.getRedirectUri(), StandardCharsets.UTF_8)
                );
        GoogleTokenResponse googleTokenResponse;
        try {
            googleTokenResponse = googleOAuthClient.getAccessToken(requestInfo);
        } catch (Exception exception) {
            throw new GoogleAuthException("Ошибка авторизации через google");
        }

        if (googleTokenResponse == null || googleTokenResponse.accessToken() == null) {
            throw new GoogleAuthException("Ошибка авторизации через google");
        }

        GoogleUserInfo googleUserInfo = googleUserInfoClient.getUserInfo("Bearer %s".formatted(googleTokenResponse.accessToken()));

        if (googleUserInfo == null || googleUserInfo.email() == null) {
            throw new GoogleAuthException("Не удалось получить информацию о пользователе");
        }

        return googleUserInfo;
    }

}
