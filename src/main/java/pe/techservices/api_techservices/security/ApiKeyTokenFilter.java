package pe.techservices.api_techservices.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.techservices.api_techservices.model.UsuarioApi;
import pe.techservices.api_techservices.service.AuthService;

public class ApiKeyTokenFilter extends OncePerRequestFilter {
    private final AuthService authService;
    public ApiKeyTokenFilter(AuthService authService) { this.authService = authService; }

    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String apiKey = request.getHeader("x-api-key");
        String authorization = request.getHeader("Authorization");
        String token = authorization != null && authorization.startsWith("Bearer ") ? authorization.substring(7).trim() : null;
        if (apiKey == null || apiKey.isBlank() || token == null || token.isBlank()) { write(response, 401, "Credenciales ausentes"); return; }
        UsuarioApi usuario = authService.autenticar(apiKey, token);
        if (usuario == null) { write(response, 401, "Credenciales incorrectas"); return; }
        if (!usuario.isEstado()) { write(response, 403, "Usuario inactivo"); return; }
        if (usuario.getTokensDisponibles() <= 0) { write(response, 403, "No hay tokens disponibles"); return; }
        var authentication = new UsernamePasswordAuthenticationToken(usuario.getId(), null, java.util.List.of());
        var securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        chain.doFilter(request, response);
    }
    private void write(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status); response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"status\":" + status + ",\"error\":\"" + (status == 401 ? "Unauthorized" : "Forbidden") + "\",\"mensaje\":\"" + message + "\"}");
    }
}
