package karebes.movies.backend.modules.user.repositories;

import karebes.movies.backend.modules.user.entities.RefreshTokenEntity;
import karebes.movies.backend.modules.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByUser(UserEntity user);

}