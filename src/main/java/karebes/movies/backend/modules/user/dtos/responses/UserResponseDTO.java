package karebes.movies.backend.modules.user.dtos.responses;

import java.util.UUID;

public record UserResponseDTO (
        UUID id,
        String username,
        String email,
        String status
) {
}