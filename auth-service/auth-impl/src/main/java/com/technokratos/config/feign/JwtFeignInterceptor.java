package com.technokratos.config.feign;

import com.technokratos.service.api.JwtService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtFeignInterceptor implements RequestInterceptor {

    private final JwtService jwtService;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String jwt = jwtService.generateAccessTokenToServices();
        requestTemplate.header("Authorization", "Bearer " + jwt);
    }
}
