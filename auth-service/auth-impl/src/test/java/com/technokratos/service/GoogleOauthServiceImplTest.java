package com.technokratos.service;

import com.technokratos.client.GoogleOAuthClient;
import com.technokratos.client.GoogleUserInfoClient;
import com.technokratos.client.dto.GoogleTokenResponse;
import com.technokratos.client.dto.GoogleUserInfo;
import com.technokratos.exception.GoogleAuthException;
import com.technokratos.service.impl.GoogleOAuthServiceImpl;
import com.technokratos.service.properties.GoogleOAuthProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GoogleOauthServiceImplTest {

    private GoogleOAuthClient googleOAuthClient;
    private GoogleUserInfoClient googleUserInfoClient;
    private GoogleOAuthServiceImpl googleOAuthService;

    @BeforeEach
    void setUp() {
        googleOAuthClient = mock(GoogleOAuthClient.class);
        googleUserInfoClient = mock(GoogleUserInfoClient.class);
        GoogleOAuthProperties googleOAuthProperties = new GoogleOAuthProperties(
                "test-client-id",
                "test-client-secret",
                List.of("openid", "email", "profile"),
                "http://localhost:8080/callback"
        );

        googleOAuthService = new GoogleOAuthServiceImpl(
                googleOAuthClient, googleUserInfoClient, googleOAuthProperties
        );
    }

    @Test
    void testGetGoogleUserInfo_success() {
        String code = "valid-auth-code";
        GoogleTokenResponse tokenResponse = new GoogleTokenResponse("access-token");
        GoogleUserInfo userInfo = new GoogleUserInfo("name", "surname", "test@gmail.com");

        when(googleOAuthClient.getAccessToken(anyString())).thenReturn(tokenResponse);
        when(googleUserInfoClient.getUserInfo("Bearer access-token")).thenReturn(userInfo);

        GoogleUserInfo result = googleOAuthService.getGoogleUserInfo(code);

        assertNotNull(result);
        assertEquals("test@gmail.com", result.email());
        assertEquals("name", result.name());
        verify(googleOAuthClient).getAccessToken(anyString());
        verify(googleUserInfoClient).getUserInfo("Bearer access-token");
    }

    @Test
    void testGetGoogleUserInfo_tokenError() {
        String code = "invalid-auth-code";
        when(googleOAuthClient.getAccessToken(anyString())).thenThrow(new RuntimeException("fail"));

        assertThrows(GoogleAuthException.class, () -> googleOAuthService.getGoogleUserInfo(code));
    }

    @Test
    void testGetGoogleUserInfo_nullTokenResponse() {
        String code = "code";
        when(googleOAuthClient.getAccessToken(anyString())).thenReturn(null);

        assertThrows(GoogleAuthException.class, () -> googleOAuthService.getGoogleUserInfo(code));
    }

    @Test
    void testGetGoogleUserInfo_nullEmail() {
        String code = "code";
        GoogleTokenResponse tokenResponse = new GoogleTokenResponse("access-token");
        when(googleOAuthClient.getAccessToken(anyString())).thenReturn(tokenResponse);
        when(googleUserInfoClient.getUserInfo("Bearer access-token")).thenReturn(new GoogleUserInfo(null, null, null));

        assertThrows(GoogleAuthException.class, () -> googleOAuthService.getGoogleUserInfo(code));
    }
}
