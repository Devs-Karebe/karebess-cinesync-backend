package karebes.movies.backend.core.exception.user;

import karebes.movies.backend.core.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidUserException extends ApiException {

    public InvalidUserException() {
        super(
                "Invalid user",
                "Usuário inválido",
                HttpStatus.BAD_REQUEST,
                "INVALID_USER"
        );
    }
}