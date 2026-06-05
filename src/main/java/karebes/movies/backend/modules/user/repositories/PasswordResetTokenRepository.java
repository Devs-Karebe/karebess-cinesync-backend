package karebes.movies.backend.modules.user.repositories;

import karebes.movies.backend.modules.user.entities.PasswordResetTokenEntity;
import karebes.movies.backend.modules.user.entities.UserEntity;
import karebes.movies.backend.shared.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {
    Optional<PasswordResetTokenEntity> findByToken(String token);
    Optional<PasswordResetTokenEntity> findByTokenAndTokenType(String token, TokenType tokenType);
    void deleteByUserAndTokenType(UserEntity user, TokenType tokenType);
}