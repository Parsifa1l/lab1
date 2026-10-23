package ru.mfa.lab3.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mfa.lab3.dto.BookEditionDto;
import ru.mfa.lab3.service.BookEditionService;

import java.util.List;

@RestController
@RequestMapping("/api/book-editions")
public class BookEditionController {

    private final BookEditionService service;

    public BookEditionController(BookEditionService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookEditionDto.Response> getAll() {
        return service.findAll().stream().map(BookEditionDto.Response::from).toList();
    }

    @GetMapping("/{id}")
    public BookEditionDto.Response getById(@PathVariable Long id) {
        return BookEditionDto.Response.from(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookEditionDto.Response create(@Valid @RequestBody BookEditionDto.Create request) {
        return BookEditionDto.Response.from(service.create(request));
    }

    @PatchMapping("/{id}")
    public BookEditionDto.Response update(@PathVariable Long id, @Valid @RequestBody BookEditionDto.Patch request) {
        return BookEditionDto.Response.from(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
