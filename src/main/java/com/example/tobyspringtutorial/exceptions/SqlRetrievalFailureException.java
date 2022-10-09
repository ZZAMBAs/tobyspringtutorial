package com.example.tobyspringtutorial.exceptions;

public class SqlRetrievalFailureException extends RuntimeException {
    public SqlRetrievalFailureException() {
        super();
    }

    public SqlRetrievalFailureException(String message) {
        super(message);
    }

    public SqlRetrievalFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlRetrievalFailureException(Throwable cause) {
        super(cause);
    }

    protected SqlRetrievalFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
