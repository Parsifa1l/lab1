package ru.mfa.lab2.dto;

import ru.mfa.lab2.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public final class UserDto {
    private UserDto() {}

    public record Create(@NotBlank String name, @NotBlank String email) {}

    public record Patch(
            @Pattern(regexp = Patterns.NOT_BLANK, message = "must not be blank") String name,
            @Pattern(regexp = Patterns.NOT_BLANK, message = "must not be blank") String email) {}

    public record Response(Long id, String name, String email) {
        public static Response from(User u) {
            return new Response(u.id(), u.name(), u.email());
        }
    }
}
