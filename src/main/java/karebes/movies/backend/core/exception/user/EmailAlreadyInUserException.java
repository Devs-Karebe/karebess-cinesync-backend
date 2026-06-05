package karebes.movies.backend.core.exception.user;

import karebes.movies.backend.core.exception.ApiException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyInUserException extends ApiException {

    public EmailAlreadyInUserException() {
        super(
                "Email already in use",
                "Este email já está sendo usado.",
                HttpStatus.CONFLICT,
                "EMAIL_ALREADY_IN_USE"
        );
    }
}
