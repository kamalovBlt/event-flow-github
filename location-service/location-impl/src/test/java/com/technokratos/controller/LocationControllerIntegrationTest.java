package com.technokratos.controller;

import com.technokratos.config.SecurityTestConfiguration;
import com.technokratos.dto.request.LocationRequest;
import com.technokratos.dto.response.LocationResponse;
import com.technokratos.dto.response.LocationShortResponse;
import com.technokratos.model.Location;
import com.technokratos.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SecurityTestConfiguration.class
)
@ActiveProfiles("test")
@Testcontainers
public class LocationControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LocationRepository locationRepository;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> "%s/location".formatted(mongoDBContainer.getConnectionString()));
    }

    private String testLocationId;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
        Location location = new Location(
                null,
                1L,
                "Test Location",
                "Description",
                null,
                10.0,
                20.0,
                null);
        testLocationId = locationRepository.save(location).getId();
    }

    @Test
    void shouldFindById() {
        ResponseEntity<LocationResponse> response = restTemplate.exchange(
                "/api/v1/location-service/locations/%s".formatted(testLocationId),
                HttpMethod.GET,
                null,
                LocationResponse.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldSaveLocation() {
        LocationRequest request = new LocationRequest(
                "New Location",
                1L,
                "New Description",
                25.0, 35.0,
                null,
                null);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/location-service/locations",
                HttpMethod.POST,
                new HttpEntity<>(request),
                String.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        String locationId = response.getBody();
        assertNotNull(locationId);
        Location savedLocation = locationRepository.findById(locationId).orElseThrow();
        assertEquals("New Location", savedLocation.getName());
    }

    @Test
    void shouldUpdateLocation() {
        LocationRequest request = new LocationRequest("Updated Location", 1L,"Updated Description", 30.0, 40.0, null, null);
        restTemplate.put("/api/v1/location-service/locations/%s".formatted(testLocationId), request);
        Location updatedLocation = locationRepository.findById(testLocationId).orElseThrow();
        assertEquals("Updated Location", updatedLocation.getName());
    }

    @Test
    void shouldDeleteLocation() {
        restTemplate.delete("/api/v1/location-service/locations/%s".formatted(testLocationId));
        assertFalse(locationRepository.existsById(testLocationId));
    }

    @Test
    void shouldGetRecommendedLocations() {
        ResponseEntity<LocationShortResponse[]> response = restTemplate.getForEntity(
                "/api/v1/location-service/locations/recommendations?page=0&size=10", LocationShortResponse[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("Test Location", response.getBody()[0].name());
    }
}