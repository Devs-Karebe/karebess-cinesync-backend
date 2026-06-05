package karebes.movies.backend.core.exception.user;

import karebes.movies.backend.core.exception.ApiException;
import org.springframework.http.HttpStatus;

public class UnauthorizedUserException extends ApiException {

    public UnauthorizedUserException() {
        super(
                "Unauthorized user",
                "Usuário não autorizado",
                HttpStatus.FORBIDDEN,
                "UNAUTHORIZED_USER"
        );
    }
}