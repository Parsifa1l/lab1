package ru.mfa.lab3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import ru.mfa.lab3.entity.Copy;
import ru.mfa.lab3.entity.CopyCondition;

public final class CopyDto {
    private CopyDto() {}

    public record Create(@NotNull Long editionId, @NotBlank String inventoryNumber, @NotNull CopyCondition condition) {}

    public record Patch(
            Long editionId,
            @Pattern(regexp = Patterns.NOT_BLANK, message = "must not be blank") String inventoryNumber,
            CopyCondition condition) {}

    public record Response(Long id, Long editionId, String inventoryNumber, CopyCondition condition) {
        public static Response from(Copy c) {
            return new Response(c.getId(), c.getEdition().getId(), c.getInventoryNumber(), c.getCondition());
        }
    }
}
