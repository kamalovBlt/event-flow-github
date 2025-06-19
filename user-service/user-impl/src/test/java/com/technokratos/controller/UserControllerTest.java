package com.technokratos.controller;

import com.technokratos.config.SecurityTestConfig;
import com.technokratos.dto.AuthProviderDTO;
import com.technokratos.dto.RoleDTO;
import com.technokratos.dto.request.UserRequest;
import com.technokratos.dto.request.UserWithOAuthRequest;
import com.technokratos.dto.response.UserResponse;
import com.technokratos.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SecurityTestConfig.class
)
@ActiveProfiles("test")
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:%s/api/v1/user-service/users".formatted(port);
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveUser() {
        UserRequest request = getValidUserRequest("save@test.com");

        ResponseEntity<Long> response = restTemplate.postForEntity(baseUrl, request, Long.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldFindUserById() {
        Long userId = restTemplate.postForEntity(baseUrl, getValidUserRequest("find@test.com"), Long.class).getBody();

        ResponseEntity<UserResponse> response = restTemplate.getForEntity("%s/%s".formatted(baseUrl, userId), UserResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo("find@test.com");
    }

    @Test
    void shouldUpdateUser() {
        Long userId = restTemplate.postForEntity(baseUrl, getValidUserRequest("update@old.com"), Long.class).getBody();

        UserRequest update = getValidUserRequest("update@new.com");
        HttpEntity<UserRequest> entity = new HttpEntity<>(update, getJsonHeaders());

        ResponseEntity<Void> updateResponse = restTemplate.exchange(
                "%s/%s".formatted(baseUrl, userId),
                HttpMethod.PUT,
                entity,
                Void.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<UserResponse> getResponse = restTemplate.getForEntity(
                "%s/%s".formatted(baseUrl, userId),
                UserResponse.class
        );
        assertThat(getResponse.getBody().email()).isEqualTo("update@new.com");
    }

    @Test
    void shouldDeleteUser() {
        Long userId = restTemplate.postForEntity(baseUrl, getValidUserRequest("delete@test.com"), Long.class).getBody();
        restTemplate.delete("%s/%s".formatted(baseUrl, userId));

        ResponseEntity<String> getResponse = restTemplate.getForEntity("%s/%s".formatted(baseUrl, userId), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void shouldReturn404IfUserNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("%s/999999".formatted(baseUrl), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    

    @Test
    void shouldReturn404WhenUpdatingNonExistingUser() {
        UserRequest update = getValidUserRequest("nonexist@update.com");
        HttpEntity<UserRequest> entity = new HttpEntity<>(update, getJsonHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                "%s/999999".formatted(baseUrl),
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingUser() {
        ResponseEntity<String> response = restTemplate.exchange(
                "%s/999999".formatted(baseUrl),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldSaveOAuthUser() {
        UserWithOAuthRequest request = new UserWithOAuthRequest(
                "oauth@test.com",
                "OAuthName",
                "OAuthLast",
                "OAuthCity",
                true,
                List.of(RoleDTO.USER),
                AuthProviderDTO.GOOGLE
        );

        HttpEntity<UserWithOAuthRequest> entity = new HttpEntity<>(request, getJsonHeaders());

        ResponseEntity<Long> response = restTemplate.postForEntity(
                "%s/oauth".formatted(baseUrl),
                entity,
                Long.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldFindUserByEmail() {
        String email = "emailtest@test.com";
        restTemplate.postForEntity(baseUrl, getValidUserRequest(email), Long.class);

        String url = "%s/email?email=%s".formatted(baseUrl, email);
        ResponseEntity<UserResponse> response = restTemplate.getForEntity(url, UserResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo(email);
    }


    @Test
    void shouldReturn404WhenUserNotFoundByEmail() {
        String url = "%s/email?email=notfound@test.com".formatted(baseUrl);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private UserRequest getValidUserRequest(String email) {
        return new UserRequest(
                email,
                "securePassword",
                "First",
                "Last",
                null,
                false,
                List.of(RoleDTO.USER),
                AuthProviderDTO.LOCAL
        );
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() {
        String url = "%s/email?email=invalid-email".formatted(baseUrl);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() {
        UserRequest request = getValidUserRequest("duplicate@test.com");

        restTemplate.postForEntity(baseUrl, request, Long.class);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("Пользователь с таким email уже существует");
        assertThat(response.getBody()).contains("EmailAlreadyExistException");
    }


    private HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
