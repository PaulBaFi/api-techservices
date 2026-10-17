package pe.techservices.api_techservices.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.techservices.api_techservices.model.UsuarioApi;

public interface UsuarioApiRepository extends JpaRepository<UsuarioApi, Long> {
    List<UsuarioApi> findByEstadoTrue();
    // Native FOR UPDATE avoids Hibernate 7 generating "FOR UPDATE OF alias",
    // which is not accepted by the MySQL/MariaDB version used locally.
    @Query(value = "select * from usuarios_api where id = :id for update", nativeQuery = true)
    Optional<UsuarioApi> findByIdForUpdate(@Param("id") Long id);
}
