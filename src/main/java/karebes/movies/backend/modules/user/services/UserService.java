package karebes.movies.backend.modules.user.services;

import jakarta.transaction.Transactional;
import karebes.movies.backend.core.exception.BusinessException;
import karebes.movies.backend.core.exception.auth.InvalidCredentialsException;
import karebes.movies.backend.core.exception.user.EmailAlreadyInUserException;
import karebes.movies.backend.modules.user.dtos.requests.UpdatePasswordRequestDTO;
import karebes.movies.backend.modules.user.dtos.requests.UpdateUserRequestDTO;
import karebes.movies.backend.modules.user.dtos.responses.UserResponseDTO;
import karebes.movies.backend.modules.user.entities.UserEntity;
import karebes.movies.backend.modules.user.repositories.RefreshTokenRepository;
import karebes.movies.backend.modules.user.repositories.UserRepository;
import karebes.movies.backend.shared.enums.Status;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository repository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder    ){
        this.userRepository = repository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO getUser(UUID userId) {

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return toUserResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUser(UUID userId, UpdateUserRequestDTO userUpdate) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (
                userUpdate.username() != null &&
                        !userUpdate.username().equals(user.getName())
        ) {

            user.setName(userUpdate.username());
        }

        if (
                userUpdate.email() != null &&
                        !userUpdate.email().equals(user.getEmail())
        ) {

            if (userRepository.existsByEmail(userUpdate.email())) {
                throw new EmailAlreadyInUserException();
            }

            user.setEmail(userUpdate.email());
        }

        userRepository.save(user);

        return toUserResponseDTO(user);
    }

    @Transactional
    public void changePassword(UUID userId, UpdatePasswordRequestDTO updatePassword) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(updatePassword.currentPassword(), user.getPassword_hash())) {
            throw new InvalidCredentialsException();
        }

        if (passwordEncoder.matches(updatePassword.newPassword(), user.getPassword_hash())) {
            throw new BusinessException(
                    "New password cannot be the same as current password",
                    "A nova senha não pode ser igual à senha atual.",
                    "SAME_PASSWORD"
            );
        }

        String encodedPassword = passwordEncoder.encode(updatePassword.newPassword());

        user.setPassword_hash(encodedPassword);

        userRepository.save(user);

    }

    @Transactional
    public void softDeleteUser(UUID userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Set deleted timestamp and status
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(Status.INACTIVE);

        // Revoke all refresh tokens for this user
        refreshTokenRepository.deleteByUser(user);

        userRepository.save(user);
    }

    private UserResponseDTO toUserResponseDTO(UserEntity user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getStatus().name()
        );
    }
}