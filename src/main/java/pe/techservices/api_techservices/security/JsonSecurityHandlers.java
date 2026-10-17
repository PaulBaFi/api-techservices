package pe.techservices.api_techservices.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

public final class JsonSecurityHandlers {
    private JsonSecurityHandlers() { }

    public static AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, exception) -> write(response, 401, "Unauthorized", "Autenticación requerida");
    }

    public static AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> write(response, 403, "Forbidden", "Acceso denegado");
    }

    private static void write(HttpServletResponse response, int status, String error, String message) throws IOException {
        if (response.isCommitted()) return;
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"status\":" + status + ",\"error\":\"" + error + "\",\"mensaje\":\"" + message + "\"}");
    }
}
