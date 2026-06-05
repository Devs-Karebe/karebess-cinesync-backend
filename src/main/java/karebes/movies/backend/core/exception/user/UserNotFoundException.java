package karebes.movies.backend.core.exception.user;

import karebes.movies.backend.core.exception.ApiException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {

    public UserNotFoundException() {
        super(
                "User not found",
                "Usuário não encontrado.",
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND"
        );
    }
}