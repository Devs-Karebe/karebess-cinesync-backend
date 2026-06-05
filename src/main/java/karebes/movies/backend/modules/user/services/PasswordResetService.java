package karebes.movies.backend.modules.user.services;

import jakarta.transaction.Transactional;
import karebes.movies.backend.core.security.jwt.JwtTokenProvider;
import karebes.movies.backend.modules.user.dtos.requests.ResetPasswordRequestDTO;
import karebes.movies.backend.modules.user.entities.PasswordResetTokenEntity;
import karebes.movies.backend.modules.user.entities.RefreshTokenEntity;
import karebes.movies.backend.modules.user.entities.UserEntity;
import karebes.movies.backend.modules.user.repositories.PasswordResetTokenRepository;
import karebes.movies.backend.modules.user.repositories.UserRepository;
import karebes.movies.backend.shared.enums.TokenType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository repository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            PasswordResetTokenRepository repository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String createResetToken(String email) {

        Optional<UserEntity> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "Se o email existir, enviaremos instruções";
        }

        UserEntity user = optionalUser.get();

        String token = UUID.randomUUID().toString();

        PasswordResetTokenEntity resetToken =
                new PasswordResetTokenEntity();

        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpirationDate(
                Instant.now().plus(15, ChronoUnit.MINUTES)
        );
        resetToken.setTokenType(
                TokenType.RESET_PASSWORD
        );

        repository.save(resetToken);

        return token;
    }

    // 🔹 RESET
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {

        PasswordResetTokenEntity resetToken = repository.findByTokenAndTokenType(request.token(), TokenType.RESET_PASSWORD)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // expiração
        if (resetToken.getExpirationDate().isBefore(Instant.now())) {
            throw new RuntimeException("Token expirado");
        }

        // validação senha
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new RuntimeException("Senhas não coincidem");
        }

        UserEntity user = resetToken.getUser();

        user.setPassword_hash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // 🔥 invalida token
        repository.delete(resetToken);
    }
}