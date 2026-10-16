package ru.mfa.lab2.dto;

import ru.mfa.lab2.model.BookEdition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public final class BookEditionDto {
    private BookEditionDto() {}

    public record Create(@NotBlank String title, @NotBlank String author, @NotBlank String isbn) {}

    public record Patch(
            @Pattern(regexp = Patterns.NOT_BLANK, message = "must not be blank") String title,
            @Pattern(regexp = Patterns.NOT_BLANK, message = "must not be blank") String author,
            @Pattern(regexp = Patterns.NOT_BLANK, message = "must not be blank") String isbn) {}

    public record Response(Long id, String title, String author, String isbn) {
        public static Response from(BookEdition e) {
            return new Response(e.id(), e.title(), e.author(), e.isbn());
        }
    }
}
