package ru.mfa.lab2.dto;

import ru.mfa.lab2.model.WaitingRequest;
import ru.mfa.lab2.model.WaitingStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class WaitingRequestDto {
    private WaitingRequestDto() {}

    /** Дату постановки и начальный статус (WAITING) задаёт сервер. */
    public record Create(@NotNull Long editionId, @NotNull Long userId) {}

    public record Patch(WaitingStatus status) {}

    public record Response(Long id, Long editionId, Long userId, LocalDateTime requestDate, WaitingStatus status) {
        public static Response from(WaitingRequest w) {
            return new Response(w.id(), w.editionId(), w.userId(), w.requestDate(), w.status());
        }
    }
}
