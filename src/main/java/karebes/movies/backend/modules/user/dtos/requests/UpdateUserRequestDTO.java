package karebes.movies.backend.modules.user.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDTO(
        @Size(min = 3, message = "Nome de usuário deve conter no mínimo 3 caracteres")
        String username,
        @Email(message = "Email informado não é válido")
        String email
) {
}