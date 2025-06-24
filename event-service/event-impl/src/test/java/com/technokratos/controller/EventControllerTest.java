package com.technokratos.controller;

import com.technokratos.config.SecurityTestConfig;
import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.dto.TicketCategoryDTO;
import com.technokratos.dto.request.EventRequest;
import com.technokratos.dto.request.ticket.TicketRequest;
import com.technokratos.dto.request.ticket.TicketsRequest;
import com.technokratos.dto.response.event.EventResponse;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SecurityTestConfig.class
)
@ActiveProfiles("test")
@Transactional
public class EventControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:%s/api/v1/event-service/events".formatted(port);
        jdbcTemplate.update("DELETE FROM event");
    }

    @Test
    void shouldSaveEvent() {
        EventRequest request = getValidEventRequest("loc1");
        ResponseEntity<Long> response = restTemplate.postForEntity(baseUrl, request, Long.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldFindEventById() {
        Long eventId = restTemplate.postForEntity(baseUrl, getValidEventRequest("loc2"), Long.class).getBody();

        ResponseEntity<EventResponse> response = restTemplate.getForEntity(
                "%s/%d".formatted(baseUrl, eventId), EventResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Test Concert");
    }

    @Test
    void shouldUpdateEvent() {
        Long eventId = restTemplate.postForEntity(baseUrl, getValidEventRequest("loc3"), Long.class).getBody();

        EventRequest updated = new EventRequest(
                "Updated Name",
                "Updated Description",
                EventCategoryDTO.NO_CATEGORY,
                "loc1",
                "hall1",
                1L,
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(3).plusHours(2),
                new TicketsRequest("100", "250",
                List.of(new TicketRequest(1L,2L,
                        TicketCategoryDTO.VIP, BigDecimal.valueOf(100)))),
                List.of()
        );

        HttpEntity<EventRequest> entity = new HttpEntity<>(updated, getJsonHeaders());

        ResponseEntity<Void> response = restTemplate.exchange(
                "%s/%d".formatted(baseUrl, eventId),
                HttpMethod.PUT,
                entity,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldDeleteEvent() {
        Long eventId = restTemplate.postForEntity(baseUrl, getValidEventRequest("loc4"), Long.class).getBody();

        restTemplate.delete("%s/%d".formatted(baseUrl, eventId));

        ResponseEntity<String> response = restTemplate.getForEntity("%s/%d".formatted(baseUrl, eventId), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404IfEventNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("%s/999999".formatted(baseUrl), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private EventRequest getValidEventRequest(String locationId) {
        return new EventRequest(
                "Test Concert",
                "An exciting live concert event.",
                EventCategoryDTO.CONCERT,
                locationId,
                "hall1",
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                new TicketsRequest("100", "250",
                        List.of(new TicketRequest(1L,2L,
                                TicketCategoryDTO.VIP, BigDecimal.valueOf(100)))),
                List.of()
        );
    }

    private HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
