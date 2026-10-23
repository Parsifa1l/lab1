package ru.mfa.lab3.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mfa.lab3.dto.WaitingRequestDto;
import ru.mfa.lab3.service.WaitingRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/waiting-requests")
public class WaitingRequestController {

    private final WaitingRequestService service;

    public WaitingRequestController(WaitingRequestService service) {
        this.service = service;
    }

    @GetMapping
    public List<WaitingRequestDto.Response> getAll() {
        return service.findAll().stream().map(WaitingRequestDto.Response::from).toList();
    }

    @GetMapping("/{id}")
    public WaitingRequestDto.Response getById(@PathVariable Long id) {
        return WaitingRequestDto.Response.from(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WaitingRequestDto.Response create(@Valid @RequestBody WaitingRequestDto.Create request) {
        return WaitingRequestDto.Response.from(service.create(request));
    }

    @PatchMapping("/{id}")
    public WaitingRequestDto.Response update(@PathVariable Long id, @Valid @RequestBody WaitingRequestDto.Patch request) {
        return WaitingRequestDto.Response.from(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
