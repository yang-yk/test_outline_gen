package com.iscas.testoutline.exception;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super("401", message);
    }

    public UnauthorizedException() {
        this("Unauthorized");
    }
} 