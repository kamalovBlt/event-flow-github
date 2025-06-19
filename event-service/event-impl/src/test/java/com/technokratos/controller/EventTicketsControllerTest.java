package com.technokratos.controller;

import com.technokratos.config.SecurityTestConfig;
import com.technokratos.dto.EventCategoryDTO;
import com.technokratos.dto.TicketCategoryDTO;
import com.technokratos.dto.request.EventRequest;
import com.technokratos.dto.request.ticket.TicketFullRequest;
import com.technokratos.dto.request.ticket.TicketRequest;
import com.technokratos.dto.request.ticket.TicketsRequest;
import com.technokratos.dto.response.ticket.TicketFullResponse;
import com.technokratos.dto.response.ticket.TicketsResponse;
import org.junit.jupiter.api.*;
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
public class EventTicketsControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:%d/api/v1/event-service/events".formatted(port);
    }

    @Test
    void shouldAddTicket() {
        Long eventId = restTemplate.postForEntity(baseUrl, getValidEventRequest("loc6"), Long.class).getBody();
        String url = "%s/%d/tickets".formatted(baseUrl, eventId);

        HttpEntity<TicketFullRequest> entity = new HttpEntity<>(getValidTicketRequest(), getJsonHeaders());
        ResponseEntity<Long> response = restTemplate.exchange(url, HttpMethod.POST, entity, Long.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldFindAllTicketsForEvent() {
        Long eventId = restTemplate.postForEntity(baseUrl, getValidEventRequest("loc5"), Long.class).getBody();
        String ticketUrl = "%s/%d/tickets".formatted(baseUrl, eventId);

        restTemplate.exchange(
                ticketUrl,
                HttpMethod.POST,
                new HttpEntity<>(getValidTicketRequest(), getJsonHeaders()),
                Long.class
        );

        ResponseEntity<TicketsResponse> response = restTemplate.getForEntity(ticketUrl, TicketsResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldFindTicketById() {
        Long eventId = restTemplate.postForEntity(baseUrl, getValidEventRequest("loc3"), Long.class).getBody();
        String ticketBaseUrl = "%s/%d/tickets".formatted(baseUrl, eventId);

        Long ticketId = restTemplate.exchange(
                ticketBaseUrl,
                HttpMethod.POST,
                new HttpEntity<>(getValidTicketRequest(), getJsonHeaders()),
                Long.class
        ).getBody();

        String ticketUrl = "%s/%d".formatted(ticketBaseUrl, ticketId);
        ResponseEntity<TicketFullResponse> response = restTemplate.getForEntity(ticketUrl, TicketFullResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldUpdateTicket() {
        Long eventId = restTemplate.postForEntity(baseUrl, getValidEventRequest("loc7"), Long.class).getBody();
        String ticketBaseUrl = "%s/%d/tickets".formatted(baseUrl, eventId);

        Long ticketId = restTemplate.exchange(
                ticketBaseUrl,
                HttpMethod.POST,
                new HttpEntity<>(getValidTicketRequest(), getJsonHeaders()),
                Long.class
        ).getBody();

        TicketFullRequest updated = new TicketFullRequest(
                "1",
                "1",
                3L,
                4L,
                TicketCategoryDTO.COMMON,
                new BigDecimal("1500.00")
        );

        String updateUrl = "%s/%d".formatted(ticketBaseUrl, ticketId);
        HttpEntity<TicketFullRequest> entity = new HttpEntity<>(updated, getJsonHeaders());

        ResponseEntity<Void> response = restTemplate.exchange(updateUrl, HttpMethod.PUT, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldDeleteTicket() {
        Long eventId = restTemplate.postForEntity(baseUrl, getValidEventRequest("loc8"), Long.class).getBody();
        String ticketBaseUrl = "%s/%d/tickets".formatted(baseUrl, eventId);

        Long ticketId = restTemplate.exchange(
                ticketBaseUrl,
                HttpMethod.POST,
                new HttpEntity<>(getValidTicketRequest(), getJsonHeaders()),
                Long.class
        ).getBody();

        String deleteUrl = "%s/%d".formatted(ticketBaseUrl, ticketId);
        restTemplate.delete(deleteUrl);

        ResponseEntity<String> response = restTemplate.getForEntity(deleteUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private TicketFullRequest getValidTicketRequest() {
        return new TicketFullRequest(
                "1",
                "1",
                5L,
                10L,
                TicketCategoryDTO.VIP,
                new BigDecimal("1500.00")
        );
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
                        List.of(new TicketRequest(1L, 2L, TicketCategoryDTO.VIP, BigDecimal.valueOf(100)))),
                List.of()
        );
    }

    private HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
