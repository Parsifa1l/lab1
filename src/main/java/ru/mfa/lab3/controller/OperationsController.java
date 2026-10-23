package ru.mfa.lab3.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mfa.lab3.dto.LoanDto;
import ru.mfa.lab3.dto.OperationDto;
import ru.mfa.lab3.dto.WaitingRequestDto;
import ru.mfa.lab3.service.LibraryOperationsService;

/** Бизнес-операции абонемента. Контроллер только принимает запрос и форматирует ответ. */
@RestController
@RequestMapping("/api/operations")
public class OperationsController {

    private final LibraryOperationsService operations;

    public OperationsController(LibraryOperationsService operations) {
        this.operations = operations;
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDto.Response checkout(@Valid @RequestBody OperationDto.Checkout request) {
        return LoanDto.Response.from(operations.checkout(request.copyId(), request.userId()));
    }

    @PostMapping("/loans/{id}/return")
    public OperationDto.ReturnResponse returnLoan(@PathVariable Long id,
                                                  @RequestBody(required = false) OperationDto.ReturnRequest request) {
        var condition = request == null ? null : request.condition();
        return OperationDto.ReturnResponse.from(operations.returnLoan(id, condition));
    }

    @PostMapping("/loans/{id}/renew")
    public LoanDto.Response renew(@PathVariable Long id) {
        return LoanDto.Response.from(operations.renewLoan(id));
    }

    @PostMapping("/waiting-list")
    @ResponseStatus(HttpStatus.CREATED)
    public WaitingRequestDto.Response joinWaitingList(@Valid @RequestBody OperationDto.JoinWaitingList request) {
        return WaitingRequestDto.Response.from(operations.joinWaitingList(request.editionId(), request.userId()));
    }

    @PostMapping("/waiting-requests/{id}/cancel")
    public WaitingRequestDto.Response cancel(@PathVariable Long id) {
        return WaitingRequestDto.Response.from(operations.cancelWaitingRequest(id));
    }
}
