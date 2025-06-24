package com.technokratos.service;

import com.technokratos.client.UserClient;
import com.technokratos.client.dto.GoogleUserInfo;
import com.technokratos.dto.AuthProviderDTO;
import com.technokratos.dto.RoleDTO;
import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.VerificationRequest;
import com.technokratos.dto.response.AuthenticationResponse;
import com.technokratos.dto.response.UserDetailsResponse;
import com.technokratos.exception.BadCredentialsException;
import com.technokratos.exception.PasswordNotMatchesException;
import com.technokratos.exception.UserNotFoundException;
import com.technokratos.model.JwtToken;
import com.technokratos.model.JwtTokenWithId;
import com.technokratos.repository.api.VerifyCodeRepository;
import com.technokratos.service.api.GoogleOAuthService;
import com.technokratos.service.api.JwtService;
import com.technokratos.service.api.VerifyCodeSender;
import com.technokratos.service.impl.JwtAuthenticationService;
import com.technokratos.util.VerifyCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationServiceTest {

    @Mock
    JwtService jwtService;
    @Mock
    UserClient userClient;
    @Mock
    GoogleOAuthService googleOAuthService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    JwtAuthenticationService jwtAuthenticationService;

    AuthenticationRequest authenticationRequest = new AuthenticationRequest("test@test.com", "password");

    VerifyCodeGenerator verifyCodeGenerator = new VerifyCodeGenerator();

    @Mock
    VerifyCodeSender verifyCodeSender;

    @Mock
    VerifyCodeRepository verifyCodeRepository;


    @BeforeEach
    void setUp() {
        jwtAuthenticationService = new JwtAuthenticationService(
                jwtService,
                passwordEncoder,
                userClient,
                googleOAuthService,
                verifyCodeSender,
                verifyCodeGenerator,
                verifyCodeRepository
        );
    }


    @Test
    void loginShouldSendVerifyCodeWhenValidRequest() {
        long id = 1;
        String email = "test@test.com";
        String password = "password";
        List<String> roles = List.of("USER");
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);
        prepareUserClient(id, email, passwordEncoder.encode(password), roles);
        doNothing().when(verifyCodeSender).send(anyString(), anyString());
        jwtAuthenticationService.login(authenticationRequest);
        verify(verifyCodeSender).send(anyString(), anyString());
    }

    @Test
    void loginShouldShouldThrowExceptionWhenInvalidPassword() {
        long id = 1;
        String email = "test@test.com";
        String password = "password";
        List<String> roles = List.of("USER");
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);
        prepareUserClient(id, email, passwordEncoder.encode("123123213"), roles);
        assertThrows(PasswordNotMatchesException.class, () -> jwtAuthenticationService.login(authenticationRequest));
    }

    @Test
    void refreshShouldReturnValidAuthenticationResponse() {
        prepareJwtServiceGenerateTokensFromRefreshToken("123");
        AuthenticationResponse authenticationResponse = jwtAuthenticationService.refresh("123");
        assertNotNull(authenticationResponse);
        String accessToken = authenticationResponse.accessToken();
        String refreshToken = authenticationResponse.refreshToken();
        assertNotNull(accessToken);
        assertNotNull(refreshToken);
        assertEquals("access", accessToken);
        assertEquals("refresh", refreshToken);
    }

    @Test
    void verifyShouldReturnValidAuthenticationResponseWhenValidCode() {
        String email = "test@test.com";
        String code = "123456";
        long userId = 1L;
        List<String> roles = List.of("USER");

        VerificationRequest verificationRequest = new VerificationRequest(email, code);

        when(verifyCodeRepository.findByEmail(email)).thenReturn(code);
        prepareUserClient(userId, email, "encodedPassword", roles);
        prepareJwtServiceGenerateTokens(userId, email, roles);

        AuthenticationResponse response = jwtAuthenticationService.verify(verificationRequest);

        assertNotNull(response);
        assertEquals("access", response.accessToken());
        assertEquals("refresh", response.refreshToken());
        verify(verifyCodeRepository).findByEmail(email);
        verify(jwtService).generateTokens(userId, email, roles);
    }

    @Test
    void verifyShouldThrowBadCredentialsExceptionWhenCodeNotFound() {
        String email = "test@test.com";
        String code = "123456";

        VerificationRequest verificationRequest = new VerificationRequest(email, code);

        when(verifyCodeRepository.findByEmail(email)).thenReturn(null);

        assertThrows(BadCredentialsException.class, () ->
                jwtAuthenticationService.verify(verificationRequest));

        verify(verifyCodeRepository).findByEmail(email);
        verifyNoInteractions(jwtService);
    }

    @Test
    void verifyShouldThrowBadCredentialsExceptionWhenCodeNotMatches() {
        String email = "test@test.com";
        String storedCode = "654321";
        String providedCode = "123456";

        VerificationRequest verificationRequest = new VerificationRequest(email, providedCode);

        when(verifyCodeRepository.findByEmail(email)).thenReturn(storedCode);

        assertThrows(BadCredentialsException.class, () ->
                jwtAuthenticationService.verify(verificationRequest));

        verify(verifyCodeRepository).findByEmail(email);
        verifyNoInteractions(jwtService);
    }

    @Test
    void loginViaGoogleShouldThrowUserNotFoundExceptionWhenUserNotExists() {
        String code = "fake-code";
        String email = "google@test.com";
        String name = "John";
        String surname = "Doe";
        GoogleUserInfo googleUserInfo = new GoogleUserInfo(name, surname, email);
        when(googleOAuthService.getGoogleUserInfo(code)).thenReturn(googleUserInfo);
        when(userClient.findByEmail(email)).thenThrow(new BadCredentialsException("User not found"));
        assertThrows(UserNotFoundException.class,() -> jwtAuthenticationService.loginViaGoogle(code));

    }

    @Test
    void loginViaGoogleShouldNotSaveUserIfExists() {
        String code = "existing-code";
        String email = "existing@test.com";
        String name = "Alice";
        String surname = "Smith";
        String role = "USER";
        long userId = 20L;
        List<String> roles = List.of(role);

        GoogleUserInfo googleUserInfo = new GoogleUserInfo(name, surname, email);
        UserDetailsResponse userDetails = new UserDetailsResponse(userId, email, "irrelevant", roles.stream().map(RoleDTO::valueOf).toList(), AuthProviderDTO.GOOGLE);

        when(googleOAuthService.getGoogleUserInfo(code)).thenReturn(googleUserInfo);
        when(userClient.findByEmail(email)).thenReturn(userDetails);
        when(jwtService.generateTokens(userId, email, roles)).thenReturn(new JwtToken("access-token", "refresh-token"));

        AuthenticationResponse response = jwtAuthenticationService.loginViaGoogle(code);

        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());

        verify(userClient, never()).saveOauthUser(any());
    }



    void prepareUserClient(long id, String email, String password, List<String> roles) {
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse(
                id,
                email,
                password,
                roles.stream().map(RoleDTO::valueOf).toList(),
                AuthProviderDTO.LOCAL
        );
        when(userClient.findByEmail(authenticationRequest.email())).thenReturn(userDetailsResponse);
    }

    void prepareJwtServiceGenerateTokens(long id, String email, List<String> roles) {
        when(jwtService.generateTokens(id, email, roles)).thenReturn(new JwtToken("access", "refresh"));
    }

    void prepareJwtServiceGenerateTokensFromRefreshToken(String token) {
        when(jwtService.generateTokensFromRefreshToken(token)).thenReturn(new JwtTokenWithId("access", "refresh", 1L));
    }

}
