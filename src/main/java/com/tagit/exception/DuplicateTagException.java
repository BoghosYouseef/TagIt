package com.tagit.exception;

public class DuplicateTagException extends RuntimeException {
    public DuplicateTagException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateTagException(String message){
        super(message);
    }
}