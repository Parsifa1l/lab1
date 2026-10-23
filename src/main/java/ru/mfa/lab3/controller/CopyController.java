package ru.mfa.lab3.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mfa.lab3.dto.CopyDto;
import ru.mfa.lab3.service.CopyService;

import java.util.List;

@RestController
@RequestMapping("/api/copies")
public class CopyController {

    private final CopyService service;

    public CopyController(CopyService service) {
        this.service = service;
    }

    @GetMapping
    public List<CopyDto.Response> getAll() {
        return service.findAll().stream().map(CopyDto.Response::from).toList();
    }

    @GetMapping("/{id}")
    public CopyDto.Response getById(@PathVariable Long id) {
        return CopyDto.Response.from(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CopyDto.Response create(@Valid @RequestBody CopyDto.Create request) {
        return CopyDto.Response.from(service.create(request));
    }

    @PatchMapping("/{id}")
    public CopyDto.Response update(@PathVariable Long id, @Valid @RequestBody CopyDto.Patch request) {
        return CopyDto.Response.from(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
