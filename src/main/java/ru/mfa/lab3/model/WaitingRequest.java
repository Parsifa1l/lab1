package ru.mfa.lab3.model;

import java.time.LocalDateTime;

public record WaitingRequest(Long id, Long editionId, Long userId, LocalDateTime requestDate, WaitingStatus status) {}
