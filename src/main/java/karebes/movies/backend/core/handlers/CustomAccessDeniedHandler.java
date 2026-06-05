package karebes.movies.backend.core.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest req,
            HttpServletResponse res,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.setContentType("application/json");

        res.getWriter().write("""
            {
              "status": 403,
              "error": "FORBIDDEN",
              "message": "Você não tem permissão para realizar esta ação"
            }
        """);
    }
}