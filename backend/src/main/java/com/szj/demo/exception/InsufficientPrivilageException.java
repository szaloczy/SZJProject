package com.szj.demo.exception;

public class InsufficientPrivilageException extends Exception{

    public InsufficientPrivilageException() {
        super();
    }

    public InsufficientPrivilageException(String message) {
        super(message);
    }

    public InsufficientPrivilageException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientPrivilageException(Throwable cause) {
        super(cause);
    }
}
