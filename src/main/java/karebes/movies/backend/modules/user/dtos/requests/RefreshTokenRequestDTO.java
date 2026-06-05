package karebes.movies.backend.modules.user.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
        @NotBlank(message = "Token de refresh é obrigatório")
        String refreshToken
) {}