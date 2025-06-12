package com.technokratos.config.feign;

import feign.Logger;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import org.springframework.context.annotation.Bean;

public class FeignGoogleClientConfiguration {

    @Bean
    public Encoder feignFormEncoder() {
        return new FormEncoder();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
