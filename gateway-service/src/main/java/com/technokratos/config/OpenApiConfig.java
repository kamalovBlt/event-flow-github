package com.technokratos.config;

import com.technokratos.config.properties.OpenApiProperties;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
@OpenAPIDefinition
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Event Flow API Documentation").version("1.0"));
    }

    /**
     * Бин является общим {@code OpenAPI} для всех сервисов
     */
    @Bean
    public GroupedOpenApi groupedOpenApi(OpenApiCustomizer openApiCustomizer) {
        return GroupedOpenApi.builder()
                .group("event-flow-api")
                .addOpenApiCustomizer(openApiCustomizer)
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Бин определяет метод, который из {@code openApiProperties} берет url для swagger
     * каждого микросервиса, получает {@code OpenAPI} объект и добавляет в общий {@code allOpenApi}
     * с проверками на null
     *
     * @param openApiProperties список url для swagger каждого микросервиса
     **/
    @Bean
    public OpenApiCustomizer openApiCustomizer(RestTemplate restTemplate, OpenApiProperties openApiProperties) {
        return allOpenApi -> {
            for (String url : openApiProperties.getApiDocsUrls()) {
                try {
                    String json = restTemplate.getForObject(url, String.class);
                    OpenAPI remoteApi = Json.mapper().readValue(json, OpenAPI.class);

                    if (remoteApi.getPaths() != null) {
                        remoteApi.getPaths().forEach(allOpenApi::path);
                    }

                    if (remoteApi.getComponents() != null) {
                        if (allOpenApi.getComponents() == null) {
                            allOpenApi.setComponents(new Components());
                        }

                        if (remoteApi.getComponents().getSchemas() != null) {
                            remoteApi.getComponents().getSchemas().forEach((name, schema) ->
                                    allOpenApi.getComponents().addSchemas(name, schema));
                        }
                        if (remoteApi.getComponents().getParameters() != null) {
                            remoteApi.getComponents().getParameters().forEach((name, param) ->
                                    allOpenApi.getComponents().addParameters(name, param));
                        }
                        if (remoteApi.getComponents().getResponses() != null) {
                            remoteApi.getComponents().getResponses().forEach((name, resp) ->
                                    allOpenApi.getComponents().addResponses(name, resp));
                        }
                    }

                    if (remoteApi.getTags() != null && !remoteApi.getTags().isEmpty()) {
                        remoteApi.getTags().forEach(allOpenApi::addTagsItem);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to load OpenAPI spec from URL: " + url, e);
                }
            }
        };
    }



}
