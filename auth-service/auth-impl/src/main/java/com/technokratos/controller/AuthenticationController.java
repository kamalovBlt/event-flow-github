package com.technokratos.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.technokratos.api.AuthenticationApi;
import com.technokratos.dto.request.*;
import com.technokratos.dto.response.AuthenticationResponse;
import com.technokratos.service.api.AuthenticationService;
import com.technokratos.service.api.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class AuthenticationController implements AuthenticationApi {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @Override
    public void login(AuthenticationRequest authenticationRequest) {
        authenticationService.login(authenticationRequest);
    }

    @Override
    public AuthenticationResponse verify(VerificationRequest verificationRequest) {
        return authenticationService.verify(verificationRequest);
    }

    @Override
    public AuthenticationResponse refresh(RefreshTokenRequest refreshTokenRequest) {
        return authenticationService.refresh(refreshTokenRequest.refreshToken());
    }

    @Override
    public Map<String, Object> key() {
        return new JWKSet(jwtService.publicKey()).toJSONObject();
    }

    @Override
    public String toServiceAccessToken() {
        return jwtService.generateAccessTokenToServices();
    }

    @Override
    public AuthenticationResponse loginViaGoogle(GoogleAuthorizationCodeRequest googleAuthorizationCodeRequest) {
        return authenticationService.loginViaGoogle(googleAuthorizationCodeRequest.code());
    }

    @Override
    public AuthenticationResponse loginViaGoogleNew(GoogleRegisterRequest googleRegisterRequest) {
        return authenticationService.loginViaGoogleWithSave(googleRegisterRequest);
    }

}
