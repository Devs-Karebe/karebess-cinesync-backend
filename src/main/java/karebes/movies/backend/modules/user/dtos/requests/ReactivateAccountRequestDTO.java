package karebes.movies.backend.modules.user.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ReactivateAccountRequestDTO(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email informado não é válido")
        String email
) {
}