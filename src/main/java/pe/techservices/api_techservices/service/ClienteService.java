package pe.techservices.api_techservices.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.techservices.api_techservices.dto.ClienteResponse;
import pe.techservices.api_techservices.exception.ApiException;
import pe.techservices.api_techservices.model.Cliente;
import pe.techservices.api_techservices.model.UsuarioApi;
import pe.techservices.api_techservices.repository.ClienteRepository;
import pe.techservices.api_techservices.repository.UsuarioApiRepository;

@Service
public class ClienteService {
    private final ClienteRepository clientes;
    private final UsuarioApiRepository usuarios;
    public ClienteService(ClienteRepository clientes, UsuarioApiRepository usuarios) { this.clientes = clientes; this.usuarios = usuarios; }

    @Transactional
    public ClienteResponse obtener(Long id, Long usuarioId) {
        UsuarioApi usuario = usuarios.findByIdForUpdate(usuarioId).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));
        if (!usuario.isEstado()) throw new ApiException(HttpStatus.FORBIDDEN, "Usuario inactivo");
        Cliente cliente = clientes.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        if (usuario.getTokensDisponibles() <= 0) throw new ApiException(HttpStatus.FORBIDDEN, "No hay tokens disponibles");
        usuario.consumirToken();
        return new ClienteResponse(ClienteResponse.ClienteData.from(cliente), usuario.getTokensDisponibles(), "Consulta realizada correctamente");
    }
}
