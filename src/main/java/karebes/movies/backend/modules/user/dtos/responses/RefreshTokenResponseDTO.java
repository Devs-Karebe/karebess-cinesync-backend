package karebes.movies.backend.modules.user.dtos.responses;

public record RefreshTokenResponseDTO(
        String accessToken,
        String refreshToken
) {}