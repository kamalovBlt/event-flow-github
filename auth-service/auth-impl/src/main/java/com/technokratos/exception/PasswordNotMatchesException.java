package com.technokratos.exception;

public class PasswordNotMatchesException extends RuntimeException {
    public PasswordNotMatchesException(String message) {
        super(message);
    }
}
