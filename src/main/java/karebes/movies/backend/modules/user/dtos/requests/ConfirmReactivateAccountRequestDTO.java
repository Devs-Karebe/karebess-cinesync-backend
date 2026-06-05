package karebes.movies.backend.modules.user.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record ConfirmReactivateAccountRequestDTO(
        @NotBlank(message = "Token é obrigatório")
        String token,
        @NotBlank(message = "Senha é obrigatória")
        String password
) {
}