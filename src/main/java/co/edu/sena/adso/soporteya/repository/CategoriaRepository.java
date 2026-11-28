package co.edu.sena.adso.soporteya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.sena.adso.soporteya.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findAllByOrderByNombreAsc();

    List<Categoria> findByActivaTrueOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}