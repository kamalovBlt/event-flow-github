package com.technokratos.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class OpenApiProperties {

    private final String[] apiDocsUrls;

    public OpenApiProperties(@Value("${springdoc.api-docs-urls}") String[] apiDocsUrls) {
        this.apiDocsUrls = apiDocsUrls;
    }

}
