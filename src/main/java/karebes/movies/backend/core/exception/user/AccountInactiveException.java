package karebes.movies.backend.core.exception.user;

import karebes.movies.backend.core.exception.ApiException;
import org.springframework.http.HttpStatus;

public class AccountInactiveException extends ApiException {

    public AccountInactiveException() {
        super(
                "User account inactive",
                "Sua conta está inativa. Solicite uma nova ativação.",
                HttpStatus.FORBIDDEN,
                "ACCOUNT_INACTIVE"
        );
    }
}
