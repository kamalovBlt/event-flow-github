package com.technokratos.controller.handler;

import com.technokratos.dto.response.UserServiceErrorResponse;
import com.technokratos.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestControllerAdvice
public class UserServiceExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public UserServiceErrorResponse handleUserNotFound(UserNotFoundException ex) {
        return UserServiceErrorResponse.builder()
                .message(ex.getMessage())
                .userId(String.valueOf(ex.getUserId()))
                .exceptionName(ex.getClass().getName())
                .build();
    }

}
