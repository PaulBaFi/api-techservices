package pe.techservices.api_techservices.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.techservices.api_techservices.dto.ClienteResponse;
import pe.techservices.api_techservices.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }
    @GetMapping("/{id}")
    public ClienteResponse obtener(@PathVariable Long id, Authentication authentication) {
        return service.obtener(id, (Long) authentication.getPrincipal());
    }
}
