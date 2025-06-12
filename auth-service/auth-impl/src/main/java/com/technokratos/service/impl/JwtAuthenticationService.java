package com.technokratos.service.impl;

import com.technokratos.client.UserClient;
import com.technokratos.client.dto.GoogleUserInfo;
import com.technokratos.dto.AuthProviderDTO;
import com.technokratos.dto.RoleDTO;
import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.GoogleRegisterRequest;
import com.technokratos.dto.request.UserWithOAuthRequest;
import com.technokratos.dto.response.AuthenticationResponse;
import com.technokratos.dto.response.UserDetailsResponse;
import com.technokratos.exception.*;
import com.technokratos.model.JwtToken;
import com.technokratos.service.api.AuthenticationService;
import com.technokratos.service.api.GoogleOAuthService;
import com.technokratos.service.api.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService implements AuthenticationService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserClient userClient;
    private final GoogleOAuthService googleOAuthService;

    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        UserDetailsResponse userDetails = userClient.findByEmail(authenticationRequest.email());
        if (userDetails == null) {
            throw new ExternalServiceException("Ошибка получения информации о пользователе. Попробуйте позже");
        }
        if (!passwordEncoder.matches(authenticationRequest.password(), userDetails.password())) {
            throw new PasswordNotMatchesException("Неправильный пароль");
        }
        JwtToken jwtToken = jwtService.generateTokens(
                userDetails.id(),
                userDetails.email(),
                userDetails.roles().stream().map(RoleDTO::toString).toList()
        );
        return new AuthenticationResponse(jwtToken.accessToken(), jwtToken.refreshToken());
    }

    @Override
    public AuthenticationResponse refresh(String refreshToken) {
        JwtToken jwtToken = jwtService.generateTokensFromRefreshToken(refreshToken);
        return new AuthenticationResponse(jwtToken.accessToken(), jwtToken.refreshToken());
    }

    @Override
    public AuthenticationResponse loginViaGoogle(String authorizationCode) {
        GoogleUserInfo googleUserInfo = googleOAuthService.getGoogleUserInfo(authorizationCode);
        UserDetailsResponse userDetails;
        try {
            userDetails = userClient.findByEmail(googleUserInfo.email());
        } catch (BadCredentialsException e) {
            throw new UserNotFoundException("Пользователь с такой почтой не найден", googleUserInfo.email(), googleUserInfo.name(), googleUserInfo.surname());
        }
        if (!userDetails.authProviderDTO().equals(AuthProviderDTO.GOOGLE)) {
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже существует");
        }

        JwtToken jwtToken = jwtService.generateTokens(
                userDetails.id(),
                userDetails.email(),
                userDetails.roles().stream().map(RoleDTO::toString).toList()
        );
        return new AuthenticationResponse(jwtToken.accessToken(), jwtToken.refreshToken());
    }

    @Override
    public AuthenticationResponse loginViaGoogleWithSave(GoogleRegisterRequest googleRegisterRequest) {
        try {
            userClient.findByEmail(googleRegisterRequest.email());
        } catch (BadCredentialsException e) {
            UserWithOAuthRequest userWithOAuthRequest = new UserWithOAuthRequest(
                    googleRegisterRequest.email(),
                    googleRegisterRequest.firstName(),
                    googleRegisterRequest.lastName(),
                    googleRegisterRequest.city(),
                    googleRegisterRequest.isPublicProfile(),
                    googleRegisterRequest.roles(),
                    AuthProviderDTO.GOOGLE
            );
            userClient.saveOauthUser(userWithOAuthRequest);
            UserDetailsResponse userDetails = userClient.findByEmail(googleRegisterRequest.email());
            JwtToken jwtToken = jwtService.generateTokens(
                    userDetails.id(),
                    userDetails.email(),
                    userDetails.roles().stream().map(RoleDTO::toString).toList()
            );
            return new AuthenticationResponse(jwtToken.accessToken(), jwtToken.refreshToken());
        }
        throw new UserAlreadyExistsException("Пользователь существует");
    }

}
