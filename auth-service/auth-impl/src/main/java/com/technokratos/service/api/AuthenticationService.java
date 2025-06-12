package com.technokratos.service.api;

import com.technokratos.dto.request.AuthenticationRequest;
import com.technokratos.dto.request.GoogleRegisterRequest;
import com.technokratos.dto.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest authenticationRequest);

    AuthenticationResponse refresh(String refreshToken);

    AuthenticationResponse loginViaGoogle(String authorizationCode);

    AuthenticationResponse loginViaGoogleWithSave(GoogleRegisterRequest googleRegisterRequest);

}
