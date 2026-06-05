package karebes.movies.backend.modules.user.dtos.responses;

public record LoginUserResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        String username
) {
}
