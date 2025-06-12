package com.technokratos.controller;

import com.technokratos.client.GoogleOAuthClient;
import com.technokratos.client.GoogleUserInfoClient;
import com.technokratos.client.UserClient;
import com.technokratos.client.dto.GoogleTokenResponse;
import com.technokratos.client.dto.GoogleUserInfo;
import com.technokratos.dto.AuthProviderDTO;
import com.technokratos.dto.RoleDTO;
import com.technokratos.dto.request.*;
import com.technokratos.dto.response.AuthServiceErrorResponse;
import com.technokratos.dto.response.AuthenticationResponse;
import com.technokratos.dto.response.GoogleUserNotFoundInformationResponse;
import com.technokratos.dto.response.UserDetailsResponse;
import com.technokratos.exception.BadCredentialsException;
import com.technokratos.model.JwtToken;
import com.technokratos.repository.impl.RedisRefreshTokenRepository;
import com.technokratos.service.api.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class AuthenticationControllerIntegrationTest {

    static final String REDIS_IMAGE_NAME = "redis:7.0-alpine";
    static final int REDIS_PORT = 6379;
    static final String REDIS_PASSWORD = "password";

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE_NAME))
            .withExposedPorts(REDIS_PORT)
            .withEnv("REDIS_PASSWORD", REDIS_PASSWORD)
            .withCommand("redis-server", "--requirepass", REDIS_PASSWORD);

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.data.redis.password", () -> REDIS_PASSWORD);
    }

    @Autowired
    TestRestTemplate testRestTemplate;

    @MockitoBean
    UserClient userClient;

    @MockitoBean
    GoogleOAuthClient googleOAuthClient;

    @MockitoBean
    GoogleUserInfoClient googleUserInfoClient;

    @Autowired
    JwtService jwtService;

    @Autowired
    RedisRefreshTokenRepository refreshTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void loginWithIncorrectPasswordShouldReturnNotFound() {
        when(userClient.findByEmail("test@test.com")).thenReturn(
                new UserDetailsResponse(1L, "test@test.com", "1234", List.of(RoleDTO.USER), AuthProviderDTO.LOCAL)
        );

        ResponseEntity<AuthServiceErrorResponse> response = testRestTemplate.exchange(
                "/api/v1/auth-service/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(new AuthenticationRequest("test@test.com", "123")),
                AuthServiceErrorResponse.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(401)));
        AuthServiceErrorResponse body = response.getBody();
        assertNotNull(body);
    }

    @Test
    void loginWithNonExistingEmailShouldReturnNotFound() {
        when(userClient.findByEmail("test@test.com")).thenThrow(new BadCredentialsException("NOT FOUND"));
        ResponseEntity<AuthServiceErrorResponse> response = testRestTemplate.exchange(
                "/api/v1/auth-service/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(new AuthenticationRequest("test@test.com", "123")),
                AuthServiceErrorResponse.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404)));
        AuthServiceErrorResponse body = response.getBody();
        assertNotNull(body);
    }

    @Test
    void loginWithCorrectCredentialsShouldReturnSuccess() {
        when(userClient.findByEmail("test@test.com")).thenReturn(
                new UserDetailsResponse(1L, "test@test.com", passwordEncoder.encode("123"), List.of(RoleDTO.USER), AuthProviderDTO.LOCAL)
        );
        ResponseEntity<AuthenticationResponse> response = testRestTemplate.exchange(
                "/api/v1/auth-service/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(new AuthenticationRequest("test@test.com", "123")),
                AuthenticationResponse.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(201)));
        AuthenticationResponse authenticationResponse = response.getBody();
        assertNotNull(authenticationResponse.accessToken());
        assertNotNull(authenticationResponse.refreshToken());
    }

    @Test
    void refreshWithCorrectRefreshTokenShouldReturnSuccess() {
        JwtToken jwtToken = jwtService.generateTokens(1L, "test@test.com", List.of("USER"));
        String refreshToken = jwtToken.refreshToken();
        refreshTokenRepository.save("test@test.com", refreshToken);
        ResponseEntity<AuthenticationResponse> response = testRestTemplate.exchange(
                "/api/v1/auth-service/auth/refresh",
                HttpMethod.POST,
                new HttpEntity<>(new RefreshTokenRequest(refreshToken)),
                AuthenticationResponse.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        AuthenticationResponse authenticationResponse = response.getBody();
        assertNotNull(authenticationResponse.accessToken());
        assertNotNull(authenticationResponse.refreshToken());
    }

    @Test
    void refreshWithIncorrectRefreshTokenShouldReturnUnauthorized() {
        ResponseEntity<AuthServiceErrorResponse> response = testRestTemplate.exchange(
                "/api/v1/auth-service/auth/refresh",
                HttpMethod.POST,
                new HttpEntity<>(new RefreshTokenRequest("12345")),
                AuthServiceErrorResponse.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(401)));
        AuthServiceErrorResponse body = response.getBody();
        assertNotNull(body);
    }

    @Test
    void refreshWithNonExistsRefreshTokenShouldReturnUnauthorized() {
        JwtToken jwtToken = jwtService.generateTokens(1L, "test@test.com", List.of("USER"));
        refreshTokenRepository.delete("test@test.com");
        String refreshToken = jwtToken.refreshToken();
        ResponseEntity<AuthServiceErrorResponse> response = testRestTemplate.exchange(
                "/api/v1/auth-service/auth/refresh",
                HttpMethod.POST,
                new HttpEntity<>(new RefreshTokenRequest(refreshToken)),
                AuthServiceErrorResponse.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(401)));
        AuthServiceErrorResponse body = response.getBody();
        assertNotNull(body);
    }

    @Test
    void keyShouldReturnValidPublicJWKSet() {
        ResponseEntity<Map> response = testRestTemplate.exchange(
                "/.well-known/jwks.json",
                HttpMethod.GET,
                new HttpEntity<>(null),
                Map.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        assertNotNull(response.getBody());
    }

    @Test
    void toServiceAccessTokenShouldReturnSuccess() {
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/auth-service/auth/access-token",
                HttpMethod.GET,
                new HttpEntity<>(null),
                String.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200)));
        assertNotNull(response.getBody());
    }

    @Test
    void loginViaGoogleShouldReturnJwtTokensWhenCredentialsCorrectAndUserWithGoogleAuthProviderExists() {
        when(userClient.findByEmail("test@test.com")).thenReturn(
                new UserDetailsResponse(1L, "test@test.com", null, List.of(RoleDTO.USER), AuthProviderDTO.GOOGLE)
        );
        when(googleOAuthClient.getAccessToken(anyString())).thenReturn(new GoogleTokenResponse("token"));
        when(googleUserInfoClient.getUserInfo(anyString())).thenReturn(
                new GoogleUserInfo("testName", "testSurname", "test@test.com")
        );
        ResponseEntity<AuthenticationResponse> response = testRestTemplate.exchange(
                "/api/v1/auth-service/auth/login/google",
                HttpMethod.POST,
                new HttpEntity<>(new GoogleAuthorizationCodeRequest("code")),
                AuthenticationResponse.class
        );
        verify(googleOAuthClient).getAccessToken(anyString());
        verify(googleUserInfoClient).getUserInfo(anyString());
        verify(userClient).findByEmail("test@test.com");
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(201)));
        AuthenticationResponse authenticationResponse = response.getBody();
        assertNotNull(authenticationResponse.accessToken());
        assertNotNull(authenticationResponse.refreshToken());
    }

    @Test
    void loginViaGoogleShouldReturnGoogleUserNotFoundInformationWhenCredentialsCorrectAndUserNotExists() {
        when(googleOAuthClient.getAccessToken(anyString())).thenReturn(new GoogleTokenResponse("token"));
        when(googleUserInfoClient.getUserInfo(anyString())).thenReturn(
                new GoogleUserInfo("testName", "testSurname", "test@test.com")
        );
        when(userClient.findByEmail("test@test.com")).thenThrow(BadCredentialsException.class);
        ResponseEntity<GoogleUserNotFoundInformationResponse> response = testRestTemplate.exchange(
                "/api/v1/auth-service/auth/login/google",
                HttpMethod.POST,
                new HttpEntity<>(new GoogleAuthorizationCodeRequest("code")),
                GoogleUserNotFoundInformationResponse.class
        );
        verify(googleOAuthClient).getAccessToken(anyString());
        verify(googleUserInfoClient).getUserInfo(anyString());
        verify(userClient).findByEmail("test@test.com");
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404)));
        GoogleUserNotFoundInformationResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.email());
        assertNotNull(body.firstName());
        assertNotNull(body.lastName());
    }

    @Test
    void loginViaGoogleShouldReturn409ConflictWhenUserExistsButWasRegisteredWithLocal() {
        when(userClient.findByEmail("test@test.com")).thenReturn(
                new UserDetailsResponse(1L, "test@test.com", null, List.of(RoleDTO.USER), AuthProviderDTO.LOCAL)
        );
        when(googleOAuthClient.getAccessToken(anyString())).thenReturn(new GoogleTokenResponse("token"));
        when(googleUserInfoClient.getUserInfo(anyString())).thenReturn(
                new GoogleUserInfo("testName", "testSurname", "test@test.com")
        );
        ResponseEntity<AuthServiceErrorResponse> response = testRestTemplate.exchange(
                "/api/v1/auth-service/auth/login/google",
                HttpMethod.POST,
                new HttpEntity<>(new GoogleAuthorizationCodeRequest("code")),
                AuthServiceErrorResponse.class
        );
        verify(googleOAuthClient).getAccessToken(anyString());
        verify(googleUserInfoClient).getUserInfo(anyString());
        verify(userClient).findByEmail("test@test.com");
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(409)));
    }

    @Test
    void loginViaGoogleNewShouldReturnJwtTokensAndSaveUser() {
        when(userClient.findByEmail("test@test.com"))
                .thenThrow(BadCredentialsException.class)
                .thenReturn(
                        new UserDetailsResponse(10L, "test@test.com", null, List.of(RoleDTO.USER), AuthProviderDTO.LOCAL)
                );
        when(userClient.saveOauthUser(any())).thenReturn(10L);
        ResponseEntity<AuthenticationResponse> response = testRestTemplate.exchange(
                "/api/v1/auth-service/auth/login/google/new",
                HttpMethod.POST,
                new HttpEntity<>(new GoogleRegisterRequest("test@test.com", "testName", "testLastName", "Kazan", true, List.of(RoleDTO.USER))),
                AuthenticationResponse.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(201)));
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().accessToken());
        assertNotNull(response.getBody().refreshToken());
    }

    @Test
    void loginViaGoogleNewShouldReturn409ConflictWhenUserExists() {
        when(userClient.findByEmail("test@test.com"))
                .thenReturn(
                        new UserDetailsResponse(10L, "test@test.com", null, List.of(RoleDTO.USER), AuthProviderDTO.LOCAL)
                );
        ResponseEntity<AuthenticationResponse> response = testRestTemplate.exchange(
                "/api/v1/auth-service/auth/login/google/new",
                HttpMethod.POST,
                new HttpEntity<>(new GoogleRegisterRequest("test@test.com", "testName", "testLastName", "Kazan", true, List.of(RoleDTO.USER))),
                AuthenticationResponse.class
        );
        assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(409)));
    }

}
