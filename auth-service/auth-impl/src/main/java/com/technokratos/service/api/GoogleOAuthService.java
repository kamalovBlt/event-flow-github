package com.technokratos.service.api;

import com.technokratos.client.dto.GoogleUserInfo;

public interface GoogleOAuthService {

    GoogleUserInfo getGoogleUserInfo(String code);

}
