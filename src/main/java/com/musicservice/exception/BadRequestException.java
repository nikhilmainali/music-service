package com.musicservice.exception;

/**
 * Custom runtime exception thrown when client input is invalid.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
