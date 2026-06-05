package karebes.movies.backend.modules.user.services;

import jakarta.transaction.Transactional;
import karebes.movies.backend.core.exception.user.UserNotFoundException;
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
public class AccountReactivationService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountReactivationService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String createReactivationToken(String email) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getDeletedAt() != null)
                .map(user -> {
                    // Delete existing reactivation tokens for this user
                    tokenRepository.deleteByUserAndTokenType(user, TokenType.REACTIVATE_ACCOUNT);

                    // Create new token
                    String token = UUID.randomUUID().toString();
                    PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();
                    resetToken.setToken(token);
                    resetToken.setUser(user);
                    resetToken.setExpirationDate(Instant.now().plus(30, ChronoUnit.MINUTES));
                    resetToken.setTokenType(TokenType.REACTIVATE_ACCOUNT);

                    tokenRepository.save(resetToken);
                    return token;
                })
                .orElse(null); // Return null if user doesn't exist or is not deleted
    }

    @Transactional
    public void confirmReactivation(String token, String password) {
        PasswordResetTokenEntity resetToken = tokenRepository
                .findByTokenAndTokenType(token, TokenType.REACTIVATE_ACCOUNT)
                .filter(t -> t.getExpirationDate().isAfter(Instant.now()))
                .orElseThrow(UserNotFoundException::new);

        UserEntity user = resetToken.getUser();

        // Reactivate user
        user.setDeletedAt(null);
        user.setStatus(Status.ACTIVE);

        // Update password if provided
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword_hash(passwordEncoder.encode(password));
        }

        userRepository.save(user);

        // Delete the token after use
        tokenRepository.delete(resetToken);
    }
}