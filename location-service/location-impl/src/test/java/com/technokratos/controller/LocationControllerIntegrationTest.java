package com.technokratos.controller;

import com.technokratos.config.KafkaMockConfig;
import com.technokratos.config.SecurityTestConfiguration;
import com.technokratos.dto.AddressDto;
import com.technokratos.dto.request.HallRequest;
import com.technokratos.dto.request.LocationRequest;
import com.technokratos.dto.request.RowRequest;
import com.technokratos.dto.request.SeatRequest;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {SecurityTestConfiguration.class, KafkaMockConfig.class}
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

    private Location testLocation;

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
        testLocation = locationRepository.save(location);
        System.out.println(testLocation.getId());
    }

    @Test
    void shouldFindById() {
        ResponseEntity<LocationResponse> response = restTemplate.exchange(
                "/api/v1/location-service/locations/%s".formatted(testLocation.getId()),
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
                new AddressDto("123 Main St", "City", "State", "12345"),
                List.of(
                        new HallRequest("Hall 1",
                                List.of(new RowRequest(1, List.of(new SeatRequest(1)))))
                )

        );

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
        LocationRequest request = new LocationRequest(
                "New Location",
                1L,
                "New Description",
                25.0, 35.0,
                new AddressDto("123 Main St", "City", "State", "12345"),
                List.of(
                        new HallRequest("Hall 1",
                                List.of(new RowRequest(1, List.of(new SeatRequest(1)))))
                )
        );
        restTemplate.put("/api/v1/location-service/locations/%s".formatted(testLocation.getId()), request);
        System.out.println(locationRepository.findAll());
        Location updatedLocation = locationRepository.findById(testLocation.getId()).orElseThrow();
        assertEquals("New Location", updatedLocation.getName());
    }

    @Test
    void shouldDeleteLocation() {
        restTemplate.delete("/api/v1/location-service/locations/%s".formatted(testLocation.getId()));
        assertFalse(locationRepository.existsById(testLocation.getId()));
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

    @Test
    void shouldGetNotFoundForInvalidId() {
        ResponseEntity<LocationResponse> response = restTemplate.exchange(
                "/api/v1/location-service/locations/invalid-id",
                HttpMethod.GET,
                null,
                LocationResponse.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}