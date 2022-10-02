package com.example.tobyspringtutorial.exceptions;

public class TestUserServiceException extends RuntimeException {
    public TestUserServiceException() {
        super();
    }

    public TestUserServiceException(String message) {
        super(message);
    }

    public TestUserServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestUserServiceException(Throwable cause) {
        super(cause);
    }
}
