package com.iscas.testoutline.exception;

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException(String message) {
        super("400", message);
    }

    public InvalidPasswordException() {
        this("Invalid password");
    }
} 