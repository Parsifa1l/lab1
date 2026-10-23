package ru.mfa.lab3.dto;

import jakarta.validation.constraints.NotNull;
import ru.mfa.lab3.entity.Loan;

import java.time.LocalDateTime;

public final class LoanDto {
    private LoanDto() {}

    /** Даты выдачи и срока возврата ставит сервер. */
    public record Create(@NotNull Long copyId, @NotNull Long userId) {}

    public record Patch(Long userId, LocalDateTime dueDate) {}

    public record Response(Long id, Long copyId, Long userId, LocalDateTime loanDate,
                           LocalDateTime dueDate, LocalDateTime returnDate, int renewals) {
        public static Response from(Loan l) {
            return new Response(l.getId(), l.getCopy().getId(), l.getUser().getId(),
                    l.getLoanDate(), l.getDueDate(), l.getReturnDate(), l.getRenewals());
        }
    }
}
