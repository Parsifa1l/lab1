package ru.mfa.lab2.model;

import java.time.LocalDateTime;

public record Loan(Long id, Long copyId, Long userId, LocalDateTime loanDate, LocalDateTime returnDate) {}
