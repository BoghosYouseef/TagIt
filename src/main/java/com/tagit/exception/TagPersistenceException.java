package com.tagit.exception;

public class TagPersistenceException extends RuntimeException {
    public TagPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}