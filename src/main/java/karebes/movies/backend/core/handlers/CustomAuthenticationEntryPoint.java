package karebes.movies.backend.core.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest req,
            HttpServletResponse res,
            AuthenticationException authException
    ) throws IOException {

        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType("application/json");

        res.getWriter().write("""
            {
              "status": 401,
              "error": "UNAUTHORIZED",
              "message": "É necessária autenticação para acessar este recurso"
            }
        """);
    }
}