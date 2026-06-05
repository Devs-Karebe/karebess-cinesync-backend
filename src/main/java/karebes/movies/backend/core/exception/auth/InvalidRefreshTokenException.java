package karebes.movies.backend.core.exception.auth;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Refresh token inválido ou expirado");
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}