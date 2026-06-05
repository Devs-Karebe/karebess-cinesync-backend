package karebes.movies.backend.modules.user.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterUserResponseDTO(
        UUID id,
        String username,
        String email,
        LocalDateTime created_at
) {
}
