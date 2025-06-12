package com.technokratos.exception;

import com.technokratos.dto.response.LocationServiceErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LocationExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public LocationServiceErrorResponse handleLocationNotFoundException(LocationNotFoundException e) {
        return LocationServiceErrorResponse.builder()
                .message(e.getMessage())
                .exceptionName(e.getClass().getSimpleName())
                .locationId(e.getLocationId() != null ? e.getLocationId().toString() : null)
                .build();
    }
}