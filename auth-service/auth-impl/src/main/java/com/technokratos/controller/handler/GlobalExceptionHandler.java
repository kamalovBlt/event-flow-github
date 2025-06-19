package com.technokratos.controller.handler;

import com.technokratos.dto.response.GoogleUserNotFoundInformationResponse;
import com.technokratos.exception.*;
import com.technokratos.dto.response.AuthServiceErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final AuthServiceErrorResponse handleExternalServiceError(ExternalServiceException exception) {
        return AuthServiceErrorResponse.builder()
                .exceptionName("ExternalServiceException")
                .message("Внутренний сервис вернул ошибку %s".formatted(exception.getMessage()))
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final AuthServiceErrorResponse handleBadCredentials(
            BadCredentialsException exception
    ) {
        return AuthServiceErrorResponse.builder()
                .exceptionName("BadCredentialsException")
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(EmailOrLoginNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final AuthServiceErrorResponse handleEmailOrLoginNotValid(
            EmailOrLoginNotValidException exception
    ) {
        return AuthServiceErrorResponse.builder()
                .exceptionName("EmailOrLoginNotValidException")
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(RefreshTokenNotValidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public final AuthServiceErrorResponse handleRefreshTokenNotValidException(RefreshTokenNotValidException exception) {
        return AuthServiceErrorResponse.builder()
                .exceptionName("RefreshTokenNotValidException")
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(GoogleAuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public final AuthServiceErrorResponse handleGoogleAuthException(GoogleAuthException exception) {
        return AuthServiceErrorResponse.builder()
                .exceptionName("GoogleAuthException")
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(JwtGenerationFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final AuthServiceErrorResponse handleJwtGenerationFailedException(JwtGenerationFailedException exception) {
        return AuthServiceErrorResponse.builder()
                .exceptionName("JwtGenerationFailedException")
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(PasswordNotMatchesException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final AuthServiceErrorResponse handlePasswordNotMatchesException(PasswordNotMatchesException exception) {
        return AuthServiceErrorResponse.builder()
                .exceptionName("PasswordNotMatchesException")
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public final AuthServiceErrorResponse handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return AuthServiceErrorResponse.builder()
                .exceptionName("UserAlreadyExistsException")
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final GoogleUserNotFoundInformationResponse handleUserNotFoundException(UserNotFoundException exception) {
        return new GoogleUserNotFoundInformationResponse(exception.getEmail(), exception.getFirstName(), exception.getLastName());
    }
}
