package ru.mfa.lab2.dto;

import ru.mfa.lab2.model.Copy;
import ru.mfa.lab2.model.CopyCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public final class CopyDto {
    private CopyDto() {}

    public record Create(@NotNull Long editionId, @NotBlank String inventoryNumber, @NotNull CopyCondition condition) {}

    public record Patch(
            Long editionId,
            @Pattern(regexp = Patterns.NOT_BLANK, message = "must not be blank") String inventoryNumber,
            CopyCondition condition) {}

    public record Response(Long id, Long editionId, String inventoryNumber, CopyCondition condition) {
        public static Response from(Copy c) {
            return new Response(c.id(), c.editionId(), c.inventoryNumber(), c.condition());
        }
    }
}
