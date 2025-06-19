package com.technokratos.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "yandex.s3")
public class YandexS3Properties {
    private String accessKeyId;
    private String secretAccessKey;
    private String bucket;
    private String endpoint;
    private String region;
}
