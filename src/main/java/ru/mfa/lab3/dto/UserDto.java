package ru.mfa.lab3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import ru.mfa.lab3.entity.User;

public final class UserDto {
    private UserDto() {}

    public record Create(@NotBlank String name, @NotBlank String email) {}

    public record Patch(
            @Pattern(regexp = Patterns.NOT_BLANK, message = "must not be blank") String name,
            @Pattern(regexp = Patterns.NOT_BLANK, message = "must not be blank") String email) {}

    public record Response(Long id, String name, String email) {
        public static Response from(User u) {
            return new Response(u.getId(), u.getName(), u.getEmail());
        }
    }
}
