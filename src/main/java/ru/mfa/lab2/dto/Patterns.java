package ru.mfa.lab2.dto;

public final class Patterns {
    /** Для PATCH: поле может отсутствовать (null), но если передано — не пустое. */
    public static final String NOT_BLANK = ".*\\S.*";

    private Patterns() {}
}
