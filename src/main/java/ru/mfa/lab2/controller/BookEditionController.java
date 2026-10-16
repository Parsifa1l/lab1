package ru.mfa.lab2.controller;

import ru.mfa.lab2.dto.BookEditionDto;
import ru.mfa.lab2.model.BookEdition;
import ru.mfa.lab2.service.BookEditionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        return BookEditionDto.Response.from(service.create(new BookEdition(null, request.title(), request.author(), request.isbn())));
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
