package pe.techservices.api_techservices.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String correo;
    private String telefono;

    protected Cliente() { }
    public Long getId() { return id; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getDni() { return dni; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
}
