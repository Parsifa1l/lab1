package ru.mfa.lab2.dto;

import ru.mfa.lab2.model.Loan;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class LoanDto {
    private LoanDto() {}

    /** Дата выдачи ставится сервером, клиент её не передаёт. */
    public record Create(@NotNull Long copyId, @NotNull Long userId) {}

    /** returned=true — вернуть книгу (дату возврата ставит сервер). */
    public record Patch(Long userId, Boolean returned) {}

    public record Response(Long id, Long copyId, Long userId, LocalDateTime loanDate, LocalDateTime returnDate) {
        public static Response from(Loan l) {
            return new Response(l.id(), l.copyId(), l.userId(), l.loanDate(), l.returnDate());
        }
    }
}
