package com.frederickamakye.smsplus.exceptions;

public class DuplicateStudentException extends RepositoryException {

    public DuplicateStudentException(String message, Throwable cause) {
        super(message, cause);
    }
}