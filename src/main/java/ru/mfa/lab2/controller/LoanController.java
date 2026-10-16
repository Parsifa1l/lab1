package ru.mfa.lab2.controller;

import ru.mfa.lab2.dto.LoanDto;
import ru.mfa.lab2.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @GetMapping
    public List<LoanDto.Response> getAll() {
        return service.findAll().stream().map(LoanDto.Response::from).toList();
    }

    @GetMapping("/{id}")
    public LoanDto.Response getById(@PathVariable Long id) {
        return LoanDto.Response.from(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDto.Response create(@Valid @RequestBody LoanDto.Create request) {
        return LoanDto.Response.from(service.create(request.copyId(), request.userId()));
    }

    @PatchMapping("/{id}")
    public LoanDto.Response update(@PathVariable Long id, @Valid @RequestBody LoanDto.Patch request) {
        return LoanDto.Response.from(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
