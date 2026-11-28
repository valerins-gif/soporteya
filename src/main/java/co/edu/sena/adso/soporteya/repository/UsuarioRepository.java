package co.edu.sena.adso.soporteya.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.sena.adso.soporteya.model.Rol;
import co.edu.sena.adso.soporteya.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Trae también las especialidades: se necesitan para saber qué tickets puede tomar
    @EntityGraph(attributePaths = "especialidades")
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "area")
    List<Usuario> findAllByOrderByRolAscNombreCompletoAsc();

    List<Usuario> findByRolAndActivoTrueOrderByNombreCompletoAsc(Rol rol);
}