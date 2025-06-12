package com.technokratos.exception;

public class EmailOrLoginNotValidException extends RuntimeException {
    public EmailOrLoginNotValidException(String message) {
        super(message);
    }
}
