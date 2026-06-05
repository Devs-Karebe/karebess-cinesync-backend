package karebes.movies.backend.core.security.jwt;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProperties {

    private final Dotenv dotenv = Dotenv.load();

    private final String secret;
    private final String issuer;
    private final long expiration;

    public JwtTokenProperties() {
        this.secret = dotenv.get("JWT_SECRET");
        this.issuer = dotenv.get("JWT_ISSUER");

        String exp = dotenv.get("JWT_EXPIRATION");
        this.expiration = exp != null ? Long.parseLong(exp) : 3600000L;
    }

    public String getSecret() { return secret; }
    public String getIssuer() { return issuer; }
    public long getExpiration() { return expiration; }
}