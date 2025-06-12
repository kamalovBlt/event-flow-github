package com.technokratos.config.feign;

import com.technokratos.client.UserClientErrorsDecoder;
import com.technokratos.service.api.JwtService;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignUserClientConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new UserClientErrorsDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor(JwtService jwtService) {
        return new JwtFeignInterceptor(jwtService);
    }

}
