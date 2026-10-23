package ru.mfa.lab3.dto;

import jakarta.validation.constraints.NotNull;
import ru.mfa.lab3.entity.WaitingRequest;
import ru.mfa.lab3.entity.WaitingStatus;

import java.time.LocalDateTime;

public final class WaitingRequestDto {
    private WaitingRequestDto() {}

    /** Дату постановки и начальный статус (WAITING) задаёт сервер. */
    public record Create(@NotNull Long editionId, @NotNull Long userId) {}

    public record Patch(WaitingStatus status) {}

    public record Response(Long id, Long editionId, Long userId, LocalDateTime requestDate,
                           WaitingStatus status, Long reservedCopyId) {
        public static Response from(WaitingRequest w) {
            return new Response(w.getId(), w.getEdition().getId(), w.getUser().getId(), w.getRequestDate(),
                    w.getStatus(), w.getReservedCopy() == null ? null : w.getReservedCopy().getId());
        }
    }
}
