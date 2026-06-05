package karebes.movies.backend.modules.user.services;

import karebes.movies.backend.core.exception.auth.InvalidRefreshTokenException;
import karebes.movies.backend.modules.user.entities.RefreshTokenEntity;
import karebes.movies.backend.modules.user.entities.UserEntity;
import karebes.movies.backend.modules.user.repositories.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshTokenService(
            RefreshTokenRepository repository
    ) {
        this.repository = repository;
    }

    public RefreshTokenEntity save(
            UserEntity user,
            String token,
            Instant expirationDate
    ) {

        RefreshTokenEntity refreshToken =
                new RefreshTokenEntity();

        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpirationDate(expirationDate);

        return repository.save(refreshToken);
    }

    public RefreshTokenEntity validate(String token) {

        RefreshTokenEntity refreshToken =
                repository.findByToken(token)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException(
                                        "Refresh token inválido"
                                ));

        if (refreshToken.getExpirationDate()
                .isBefore(Instant.now())) {

            throw new InvalidRefreshTokenException(
                    "Refresh token expirado"
            );
        }

        return refreshToken;
    }

    public void delete(RefreshTokenEntity token) {
        repository.delete(token);
    }
}