package karebes.movies.backend.core.exception.auth;

import karebes.movies.backend.core.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApiException {

    public InvalidCredentialsException() {
        super(
                "Invalid credentials",
                "Email ou senha inválidos.",
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS"
        );
    }
}
