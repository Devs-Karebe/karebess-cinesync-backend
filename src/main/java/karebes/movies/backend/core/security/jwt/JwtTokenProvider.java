package karebes.movies.backend.core.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import karebes.movies.backend.modules.user.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtTokenProvider {

    private final JwtTokenProperties properties;
    private final Algorithm algorithm;


    public JwtTokenProvider(JwtTokenProperties properties) {
        this.properties = properties;
        this.algorithm = Algorithm.HMAC256(properties.getSecret());
    }

    public String generateAccessToken(UserEntity user) {

        return JWT.create()
                .withIssuer(properties.getIssuer())
                .withSubject(user.getId().toString())
                .withExpiresAt(new Date(
                        Instant.now()
                                .plusMillis(properties.getExpiration())
                                .toEpochMilli()
                ))
                .sign(algorithm);
    }

    public String generateRefreshToken(UserEntity user) {

        return JWT.create()
                .withIssuer(properties.getIssuer())
                .withSubject(user.getId().toString())
                .withClaim("token_type", "refresh")
                .withExpiresAt(
                        Date.from(
                                Instant.now()
                                        .plus(7, ChronoUnit.DAYS)
                        )
                )
                .sign(algorithm);
    }

    public String validateAndGetSubject(String token) {
        return JWT.require(algorithm)
                .withIssuer(properties.getIssuer())
                .build()
                .verify(token)
                .getSubject();
    }

    public long getExpirationInSeconds() {
        return properties.getExpiration() / 1000;
    }
}