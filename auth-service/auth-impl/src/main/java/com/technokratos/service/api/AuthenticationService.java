package com.technokratos.service.api;

import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.GoogleRegisterRequest;
import com.technokratos.dto.request.VerificationRequest;
import com.technokratos.dto.response.AuthenticationResponse;

public interface AuthenticationService {

    void login(AuthenticationRequest authenticationRequest);

    AuthenticationResponse verify(VerificationRequest verificationRequest);

    AuthenticationResponse refresh(String refreshToken);

    AuthenticationResponse loginViaGoogle(String authorizationCode);

    AuthenticationResponse loginViaGoogleWithSave(GoogleRegisterRequest googleRegisterRequest);

}
