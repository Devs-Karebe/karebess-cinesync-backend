package karebes.movies.backend.modules.user.repositories;

import jakarta.validation.constraints.Email;
import karebes.movies.backend.modules.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(@Email String email);
    Optional<UserEntity> findByIdAndDeletedAtIsNull(UUID id);
}
