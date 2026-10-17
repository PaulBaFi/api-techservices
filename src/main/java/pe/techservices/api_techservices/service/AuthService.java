package pe.techservices.api_techservices.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.techservices.api_techservices.model.UsuarioApi;
import pe.techservices.api_techservices.repository.UsuarioApiRepository;

@Service
public class AuthService {
    private final UsuarioApiRepository usuarios;
    private final PasswordEncoder encoder;
    public AuthService(UsuarioApiRepository usuarios, PasswordEncoder encoder) { this.usuarios = usuarios; this.encoder = encoder; }

    public UsuarioApi autenticar(String apiKey, String token) {
        return usuarios.findAll().stream()
                .filter(u -> encoder.matches(apiKey, u.getApiKeyHash()) && encoder.matches(token, u.getTokenHash()))
                .findFirst().orElse(null);
    }
}
