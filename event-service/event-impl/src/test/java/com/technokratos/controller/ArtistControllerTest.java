package com.technokratos.controller;

import com.technokratos.config.SecurityTestConfig;
import com.technokratos.dto.request.ArtistRequest;
import com.technokratos.dto.response.artist.ArtistResponse;
import com.technokratos.repository.interfaces.ArtistRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SecurityTestConfig.class
)
@ActiveProfiles("test")
@Transactional
public class ArtistControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String baseUrl;


    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:%s/api/v1/event-service/artists".formatted(port);
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM event");
    }

    @Test
    void shouldFindAllArtistsWithoutKeywords() {
        ArtistRequest request = getValidArtistRequest("John", "Doe", "JD", "Famous pop artist", 1L, List.of());
        restTemplate.postForEntity(baseUrl, request, Long.class);

        String url = "%s?page=0&size=10".formatted(baseUrl);
        ResponseEntity<ArtistResponse[]> response = restTemplate.getForEntity(url, ArtistResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void shouldFindArtistsByKeyword() {
        ArtistRequest request1 = getValidArtistRequest("John", "Doe", "JDA", "Famous pop artist", 1L, List.of());
        ArtistRequest request2 = getValidArtistRequest("John", "Doe", "lALA", "Famous pop artist", 1L, List.of());

        restTemplate.postForEntity(baseUrl, request1, Long.class);
        restTemplate.postForEntity(baseUrl, request2, Long.class);

        String url = "%s/search?keywords=JDA&page=0&size=10".formatted(baseUrl);
        ResponseEntity<ArtistResponse[]> response = restTemplate.getForEntity(url, ArtistResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).anyMatch(a -> a.nickname().equals("JDA"));

    }

    @Test
    void shouldSaveArtist() {
        ArtistRequest request = getValidArtistRequest("John", "Doe", "JDH", "Famous pop artist", 1L, List.of());

        ResponseEntity<Long> response = restTemplate.postForEntity(baseUrl, request, Long.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldFindArtistById() {
        Long artistId = restTemplate.postForEntity(baseUrl,
                        getValidArtistRequest("Jane", "Smith", null, "Singer and songwriter", 2L, List.of()), Long.class)
                .getBody();

        ResponseEntity<ArtistResponse> response = restTemplate.getForEntity("%s/%s".formatted(baseUrl, artistId), ArtistResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().firstName()).isEqualTo("Jane");
        assertThat(response.getBody().lastName()).isEqualTo("Smith");
    }

    @Test
    void shouldUpdateArtist() {
        Long artistId = restTemplate.postForEntity(baseUrl,
                        getValidArtistRequest("Old", "Artist", null, "Old description", 3L, null), Long.class)
                .getBody();

        ArtistRequest update = getValidArtistRequest("Updated", "Artist", "Upd", "Updated description", 3L, List.of());
        HttpEntity<ArtistRequest> entity = new HttpEntity<>(update, getJsonHeaders());

        ResponseEntity<Void> updateResponse = restTemplate.exchange(
                "%s/%s".formatted(baseUrl, artistId),
                HttpMethod.PUT,
                entity,
                Void.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ArtistResponse> getResponse = restTemplate.getForEntity(
                "%s/%s".formatted(baseUrl, artistId),
                ArtistResponse.class
        );
        assertThat(getResponse.getBody().firstName()).isEqualTo("Updated");
        assertThat(getResponse.getBody().nickname()).isEqualTo("Upd");
    }

    @Test
    void shouldDeleteArtist() {
        Long artistId = restTemplate.postForEntity(baseUrl,
                        getValidArtistRequest("Delete", "Me", null, "To be deleted", 4L, null), Long.class)
                .getBody();

        restTemplate.delete("%s/%s".formatted(baseUrl, artistId));

        ResponseEntity<String> getResponse = restTemplate.getForEntity("%s/%s".formatted(baseUrl, artistId), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404IfArtistNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("%s/999999".formatted(baseUrl), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingArtist() {
        ArtistRequest update = getValidArtistRequest("Nonexist", "Artist", null, "Doesn't exist", 999L, null);
        HttpEntity<ArtistRequest> entity = new HttpEntity<>(update, getJsonHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                "%s/999999".formatted(baseUrl),
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingArtist() {
        ResponseEntity<String> response = restTemplate.exchange(
                "%s/999999".formatted(baseUrl),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private ArtistRequest getValidArtistRequest(String firstName, String lastName, String nickname, String description, Long userId, List<Long> eventIds) {
        return new ArtistRequest(
                firstName,
                lastName,
                nickname,
                description,
                userId,
                eventIds
        );
    }

    private HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
