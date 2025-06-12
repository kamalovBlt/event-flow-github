package com.technokratos.client;

import com.technokratos.client.dto.GoogleTokenResponse;
import com.technokratos.config.feign.FeignGoogleClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "google-oauth", url = "https://oauth2.googleapis.com", configuration = FeignGoogleClientConfiguration.class)
public interface GoogleOAuthClient {

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    GoogleTokenResponse getAccessToken(String googleParameters);

}
