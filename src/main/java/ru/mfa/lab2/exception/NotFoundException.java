package ru.mfa.lab2.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entity, Long id) {
        super(entity + " with id " + id + " not found");
    }
}
