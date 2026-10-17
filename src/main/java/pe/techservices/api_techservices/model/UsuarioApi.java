package pe.techservices.api_techservices.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios_api")
public class UsuarioApi {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(name = "api_key_hash", nullable = false) private String apiKeyHash;
    @Column(name = "token_hash", nullable = false) private String tokenHash;
    @Column(name = "tokens_disponibles", nullable = false) private int tokensDisponibles;
    @Column(nullable = false) private boolean estado;

    protected UsuarioApi() { }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApiKeyHash() { return apiKeyHash; }
    public String getTokenHash() { return tokenHash; }
    public int getTokensDisponibles() { return tokensDisponibles; }
    public boolean isEstado() { return estado; }
    public void consumirToken() { tokensDisponibles--; }
}
