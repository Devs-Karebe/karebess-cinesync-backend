package karebes.movies.backend.modules.user.services;

import jakarta.transaction.Transactional;
import karebes.movies.backend.core.exception.auth.InvalidCredentialsException;
import karebes.movies.backend.core.exception.user.EmailAlreadyInUserException;
import karebes.movies.backend.core.security.jwt.JwtTokenProvider;
import karebes.movies.backend.core.security.principal.UserPrincipal;
import karebes.movies.backend.modules.user.dtos.requests.LoginUserRequestDTO;
import karebes.movies.backend.modules.user.dtos.requests.RegisterUserRequestDTO;
import karebes.movies.backend.modules.user.dtos.responses.LoginUserResponseDTO;
import karebes.movies.backend.modules.user.dtos.responses.RefreshTokenResponseDTO;
import karebes.movies.backend.modules.user.dtos.responses.RegisterUserResponseDTO;
import karebes.movies.backend.modules.user.entities.RefreshTokenEntity;
import karebes.movies.backend.modules.user.entities.UserEntity;
import karebes.movies.backend.modules.user.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtToken;
    private final RefreshTokenService refreshTokenService;
    private final UserValidationService userValidationService;

    public AuthService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtToken,
            RefreshTokenService refreshTokenService,
            UserValidationService userValidationService
    ){
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtToken = jwtToken;
        this.refreshTokenService = refreshTokenService;
        this.userValidationService = userValidationService;
    }

    @Transactional
    public RegisterUserResponseDTO registerUser(RegisterUserRequestDTO dataUser) {

        if (userRepository.findByEmail(dataUser.email()).isPresent()){
            throw new EmailAlreadyInUserException();
        }

        UserEntity user = new UserEntity(
                dataUser.name(),
                dataUser.email(),
                passwordEncoder.encode(dataUser.password())
        );

        userRepository.save(user);

        return new RegisterUserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                LocalDateTime.now()
        );
    }

    public LoginUserResponseDTO loginUser(LoginUserRequestDTO dataUser) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    dataUser.email(),
                    dataUser.password()
            );

            Authentication authentication = authenticationManager.authenticate(authToken);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            UserEntity user = userPrincipal.getUser();

            userValidationService.validateUserForLogin(user);

            String accessToken =
                    jwtToken.generateAccessToken(user);

            String refreshToken =
                    jwtToken.generateRefreshToken(user);

            refreshTokenService.save(
                    user,
                    refreshToken,
                    Instant.now().plus(7, ChronoUnit.DAYS)
            );

            return new LoginUserResponseDTO(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtToken.getExpirationInSeconds(),
                    user.getName()
            );

        }catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException();
        }

    }

    @Transactional
    public RefreshTokenResponseDTO refreshToken(
            String requestToken
    ) {

        RefreshTokenEntity refreshToken =
                refreshTokenService.validate(requestToken);

        UserEntity user =
                refreshToken.getUser();

        userValidationService.validateUserForRefresh(user);

        String newAccessToken =
                jwtToken.generateAccessToken(user);

        String newRefreshToken =
                jwtToken.generateRefreshToken(user);

        refreshTokenService.delete(refreshToken);

        refreshTokenService.save(
                user,
                newRefreshToken,
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        return new RefreshTokenResponseDTO(
                newAccessToken,
                newRefreshToken
        );
    }

    @Transactional
    public void logout(String refreshToken) {
        try {
            RefreshTokenEntity token = refreshTokenService.validate(refreshToken);
            refreshTokenService.delete(token);
        } catch (Exception e) {

        }
    }

}
