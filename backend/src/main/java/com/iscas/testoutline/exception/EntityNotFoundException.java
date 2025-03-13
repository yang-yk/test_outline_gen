package com.iscas.testoutline.exception;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String message) {
        super("404", message);
    }

    public EntityNotFoundException() {
        this("Entity not found");
    }
} 