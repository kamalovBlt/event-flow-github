package com.technokratos.client;

import com.technokratos.exception.BadCredentialsException;
import com.technokratos.exception.EmailOrLoginNotValidException;
import com.technokratos.exception.ExternalServiceException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class UserClientErrorsDecoderTest {

    private final ErrorDecoder errorDecoder = new UserClientErrorsDecoder();

    private Response createResponse(int status, String reason) {
        return Response.builder()
                .status(status)
                .reason(reason)
                .request(Request.create(
                        Request.HttpMethod.GET,
                        "test",
                        Collections.emptyMap(),
                        null,
                        StandardCharsets.UTF_8,
                        new RequestTemplate()
                ))
                .build();
    }

    @Test
    void shouldThrowEmailOrLoginNotValidExceptionWhenStatusIs400() {
        Response response = createResponse(400, "Bad Request");

        Exception exception = errorDecoder.decode("methodKey", response);

        assertInstanceOf(EmailOrLoginNotValidException.class, exception);
        assertEquals("Неправильный формат электронной почты", exception.getMessage());
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenStatusIs404() {
        Response response = createResponse(404, "Not Found");

        Exception exception = errorDecoder.decode("methodKey", response);

        assertInstanceOf(BadCredentialsException.class, exception);
        assertTrue(exception.getMessage().contains("Пользователь с такими данными не найден"));
    }

    @Test
    void shouldThrowExternalServiceExceptionWhenStatusIs500() {
        Response response = createResponse(500, "Internal Server Error");
        Exception exception = errorDecoder.decode("methodKey", response);
        assertInstanceOf(ExternalServiceException.class, exception);
    }

    @Test
    void shouldThrowGenericExceptionWhenStatusIsUnknown() {
        Response response = createResponse(418, "I'm a teapot");

        Exception exception = errorDecoder.decode("methodKey", response);
        assertEquals(Exception.class, exception.getClass());
    }

}
