package com.musicservice.exception;

/**
 * Custom runtime exception thrown when a requested resource (Music, User) cannot be found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
