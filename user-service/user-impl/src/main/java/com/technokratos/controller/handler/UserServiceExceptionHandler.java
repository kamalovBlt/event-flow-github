package com.technokratos.controller.handler;

import com.technokratos.dto.response.UserServiceErrorResponse;
import com.technokratos.exception.EmailAlreadyExistException;
import com.technokratos.exception.UserNotFoundException;
import com.technokratos.exception.UserServiceException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestControllerAdvice
public class UserServiceExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public UserServiceErrorResponse handleUserNotFound(UserServiceException ex) {
        return handleUserServiceExceptionHandler(ex);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public UserServiceErrorResponse handleEmailAlreadyExist(UserServiceException ex) {
        return handleUserServiceExceptionHandler(ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public UserServiceErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        return UserServiceErrorResponse.builder()
                .exceptionName(ex.getClass().getName())
                .message(ex.getMessage())
                .build();
    }

    private UserServiceErrorResponse handleUserServiceExceptionHandler(UserServiceException ex) {
        UserServiceErrorResponse
                .UserServiceErrorResponseBuilder builder = UserServiceErrorResponse.builder()
                .message(ex.getMessage())
                .exceptionName(ex.getClass().getName());

        if (ex.getUserId() != null) {
            builder.userId(String.valueOf(ex.getUserId()));
        }
        return builder.build();
    }
}
