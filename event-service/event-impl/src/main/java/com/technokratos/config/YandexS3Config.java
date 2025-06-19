package com.technokratos.config;

import com.technokratos.config.properties.YandexS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class YandexS3Config {

    private final YandexS3Properties s3Props;

    @Bean
    public AwsCredentials awsCredentials() {
        return AwsBasicCredentials.create(s3Props.getAccessKeyId(), s3Props.getSecretAccessKey());
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .httpClient(ApacheHttpClient.create())
                .region(Region.of(s3Props.getRegion()))
                .endpointOverride(URI.create(s3Props.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials()))
                .build();
    }

}
