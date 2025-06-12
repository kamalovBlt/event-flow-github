package com.technokratos.client;

import com.technokratos.client.dto.GoogleUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "google-userinfo", url = "https://openidconnect.googleapis.com")
public interface GoogleUserInfoClient {
    @GetMapping("/v1/userinfo")
    GoogleUserInfo getUserInfo(@RequestHeader("Authorization") String authorization);
}
