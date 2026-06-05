package karebes.movies.backend.modules.user.dtos.requests;

public record ResetPasswordRequestDTO(
        String token,
        String newPassword,
        String confirmPassword
) {
}