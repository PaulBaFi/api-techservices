package pe.techservices.api_techservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.techservices.api_techservices.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> { }
