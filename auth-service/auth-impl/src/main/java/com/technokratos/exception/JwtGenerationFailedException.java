package com.technokratos.exception;

public class JwtGenerationFailedException extends RuntimeException {
    public JwtGenerationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
