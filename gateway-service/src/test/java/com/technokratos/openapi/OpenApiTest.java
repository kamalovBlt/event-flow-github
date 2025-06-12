package com.technokratos.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.technokratos.config.OpenApiConfig;
import com.technokratos.config.properties.OpenApiProperties;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest(
        classes = {OpenApiConfig.class, OpenApiProperties.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("test")
public class OpenApiTest {

    @Autowired
    private OpenApiCustomizer openApiCustomizer;

    @Autowired
    private OpenApiProperties openApiProperties;

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private GroupedOpenApi groupedOpenApi;

    @Test
    void testOpenApiPropertiesNotNullAndHaveLengthTwoAndHaveCorrectUrls() {
        String[] urls = this.openApiProperties.getApiDocsUrls();
        assertNotNull(urls);
        assertEquals(2, urls.length);
        assertEquals("http://mock-service-1/v3/api-docs", urls[0]);
        assertEquals("http://mock-service-2/v3/api-docs", urls[1]);
    }

    @Test
    void testOpenApiCustomizerLoadsRemoteApiDocs() throws JsonProcessingException {

        String[] urls = openApiProperties.getApiDocsUrls();

        Parameter testParam = new Parameter();
        testParam.setName("123");

        OpenAPI mockOpenAPI1 = new OpenAPI()
                .paths(new Paths().addPathItem("/mock1", new PathItem().addParametersItem(testParam)))
                .components(new Components().schemas(Map.of("Mock1", new Schema<>())));

        OpenAPI mockOpenAPI2 = new OpenAPI()
                .paths(new Paths().addPathItem("/mock2", new PathItem()))
                .components(new Components().schemas(Map.of("Mock2", new Schema<>())));

        String mockOpenApi1Json = Json.mapper().writeValueAsString(mockOpenAPI1);
        String mockOpenApi2Json = Json.mapper().writeValueAsString(mockOpenAPI2);

        when(restTemplate.getForObject(urls[0], String.class)).thenReturn(mockOpenApi1Json);
        when(restTemplate.getForObject(urls[1], String.class)).thenReturn(mockOpenApi2Json);

        OpenAPI grouppedOpenApi = new OpenAPI();
        openApiCustomizer.customise(grouppedOpenApi);

        assertTrue(grouppedOpenApi.getPaths().containsKey("/mock1"));
        assertTrue(grouppedOpenApi.getPaths().containsKey("/mock2"));
        assertNotNull(grouppedOpenApi.getComponents());

    }

    @Test
    void testGroupedOpenApiContainsCustomizer() {
        Set<OpenApiCustomizer> openApiCustomizers = groupedOpenApi.getOpenApiCustomizers();
        assertNotNull(openApiCustomizers);
        assertTrue(openApiCustomizers.contains(openApiCustomizer));
    }

}
