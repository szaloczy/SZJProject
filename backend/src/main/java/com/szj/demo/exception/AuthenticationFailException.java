package com.szj.demo.exception;

public class AuthenticationFailException extends IllegalStateException {
    public AuthenticationFailException(String msg) {
        super(msg);
    }
}
