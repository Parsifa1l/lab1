package ru.mfa.lab3.dto;

import jakarta.validation.constraints.NotNull;
import ru.mfa.lab3.entity.CopyCondition;
import ru.mfa.lab3.service.LibraryOperationsService;

public final class OperationDto {
    private OperationDto() {}

    public record Checkout(@NotNull Long copyId, @NotNull Long userId) {}

    /** Необязательное тело при возврате: состояние, в котором вернули книгу. */
    public record ReturnRequest(CopyCondition condition) {}

    public record JoinWaitingList(@NotNull Long editionId, @NotNull Long userId) {}

    /** reservation != null, если после возврата экземпляр закреплён за следующим читателем из очереди. */
    public record ReturnResponse(LoanDto.Response loan, boolean overdue, WaitingRequestDto.Response reservation) {
        public static ReturnResponse from(LibraryOperationsService.ReturnResult r) {
            return new ReturnResponse(
                    LoanDto.Response.from(r.loan()),
                    r.overdue(),
                    r.reservation() == null ? null : WaitingRequestDto.Response.from(r.reservation()));
        }
    }
}
