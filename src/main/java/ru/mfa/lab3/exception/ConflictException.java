package ru.mfa.lab3.exception;

/** Операция запрещена в текущем состоянии данных (бизнес-правило нарушено). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
