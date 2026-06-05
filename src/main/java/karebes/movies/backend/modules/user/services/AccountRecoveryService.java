package karebes.movies.backend.modules.user.services;

import jakarta.transaction.Transactional;
import karebes.movies.backend.core.exception.user.UserNotFoundException;
import karebes.movies.backend.modules.user.dtos.requests.ResetPasswordRequestDTO;
import karebes.movies.backend.modules.user.entities.PasswordResetTokenEntity;
import karebes.movies.backend.modules.user.entities.UserEntity;
import karebes.movies.backend.modules.user.repositories.PasswordResetTokenRepository;
import karebes.movies.backend.modules.user.repositories.UserRepository;
import karebes.movies.backend.shared.enums.Status;
import karebes.movies.backend.shared.enums.TokenType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AccountRecoveryService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountRecoveryService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Password recovery methods
    public String forgotPassword(String email) {
        var optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "Se o email existir, enviaremos instruções";
        }

        UserEntity user = optionalUser.get();
        String token = UUID.randomUUID().toString();

        PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpirationDate(Instant.now().plus(15, ChronoUnit.MINUTES));
        resetToken.setTokenType(TokenType.RESET_PASSWORD);

        tokenRepository.save(resetToken);

        return token;
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        PasswordResetTokenEntity resetToken = tokenRepository.findByTokenAndTokenType(request.token(), TokenType.RESET_PASSWORD)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.getExpirationDate().isBefore(Instant.now())) {
            throw new RuntimeException("Token expirado");
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new RuntimeException("Senhas não coincidem");
        }

        UserEntity user = resetToken.getUser();
        user.setPassword_hash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }

    // Account reactivation methods
    @Transactional
    public String requestReactivation(String email) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getDeletedAt() != null)
                .map(user -> {
                    tokenRepository.deleteByUserAndTokenType(user, TokenType.REACTIVATE_ACCOUNT);

                    String token = UUID.randomUUID().toString();
                    PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();
                    resetToken.setToken(token);
                    resetToken.setUser(user);
                    resetToken.setExpirationDate(Instant.now().plus(30, ChronoUnit.MINUTES));
                    resetToken.setTokenType(TokenType.REACTIVATE_ACCOUNT);

                    tokenRepository.save(resetToken);
                    return token;
                })
                .orElse(null);
    }

    @Transactional
    public void confirmReactivation(String token, String password) {
        PasswordResetTokenEntity resetToken = tokenRepository
                .findByTokenAndTokenType(token, TokenType.REACTIVATE_ACCOUNT)
                .filter(t -> t.getExpirationDate().isAfter(Instant.now()))
                .orElseThrow(UserNotFoundException::new);

        UserEntity user = resetToken.getUser();

        user.setDeletedAt(null);
        user.setStatus(Status.ACTIVE);

        if (password != null && !password.trim().isEmpty()) {
            user.setPassword_hash(passwordEncoder.encode(password));
        }

        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}
