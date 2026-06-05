package karebes.movies.backend.modules.user.services;

import karebes.movies.backend.core.exception.user.AccountInactiveException;
import karebes.movies.backend.core.exception.user.UnauthorizedUserException;
import karebes.movies.backend.core.exception.user.UserNotFoundException;
import karebes.movies.backend.modules.user.entities.UserEntity;
import karebes.movies.backend.shared.enums.Status;
import org.springframework.stereotype.Service;

@Service
public class UserValidationService {

    public void validateUserAccess(UserEntity user) {
        validateNotDeleted(user);

        if (user.getStatus() != Status.ACTIVE) {
            throw new UnauthorizedUserException();
        }
    }

    public void validateUserForLogin(UserEntity user) {
        validateNotDeleted(user);

        switch (user.getStatus()) {
            case INACTIVE -> throw new AccountInactiveException();
        }
    }

    public void validateUserForRefresh(UserEntity user) {
        validateNotDeleted(user);

        if (user.getStatus() != Status.ACTIVE) {
            throw new UnauthorizedUserException();
        }
    }

    private void validateNotDeleted(UserEntity user) {
        if (user.getDeletedAt() != null) {
            throw new UserNotFoundException();
        }
    }
}