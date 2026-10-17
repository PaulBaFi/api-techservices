package pe.techservices.api_techservices.dto;

import pe.techservices.api_techservices.model.Cliente;

public record ClienteResponse(ClienteData cliente, int tokensRestantes, String mensaje) {
    public record ClienteData(Long id, String nombres, String apellidos, String dni, String correo, String telefono) {
        public static ClienteData from(Cliente c) { return new ClienteData(c.getId(), c.getNombres(), c.getApellidos(), c.getDni(), c.getCorreo(), c.getTelefono()); }
    }
}
