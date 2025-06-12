package com.technokratos.service;

import com.technokratos.client.UserClient;
import com.technokratos.client.dto.GoogleUserInfo;
import com.technokratos.dto.AuthProviderDTO;
import com.technokratos.dto.RoleDTO;
import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.response.AuthenticationResponse;
import com.technokratos.dto.response.UserDetailsResponse;
import com.technokratos.exception.BadCredentialsException;
import com.technokratos.exception.PasswordNotMatchesException;
import com.technokratos.exception.UserNotFoundException;
import com.technokratos.model.JwtToken;
import com.technokratos.service.api.GoogleOAuthService;
import com.technokratos.service.api.JwtService;
import com.technokratos.service.impl.JwtAuthenticationService;
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

    @BeforeEach
    void setUp() {
        jwtAuthenticationService = new JwtAuthenticationService(
                jwtService,
                passwordEncoder,
                userClient,
                googleOAuthService
        );
    }


    @Test
    void loginShouldReturnValidAuthenticationResponse() {
        long id = 1;
        String email = "test@test.com";
        String password = "password";
        List<String> roles = List.of("USER");
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);
        prepareUserClient(id, email, passwordEncoder.encode(password), roles);
        prepareJwtServiceGenerateTokens(id, email, roles);
        AuthenticationResponse authenticationResponse = jwtAuthenticationService.login(authenticationRequest);
        assertNotNull(authenticationResponse);
        String accessToken = authenticationResponse.accessToken();
        String refreshToken = authenticationResponse.refreshToken();
        assertNotNull(accessToken);
        assertNotNull(refreshToken);
        assertEquals("access", accessToken);
        assertEquals("refresh", refreshToken);
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
        when(jwtService.generateTokensFromRefreshToken(token)).thenReturn(new JwtToken("access", "refresh"));
    }

}
