package co.edu.sena.adso.soporteya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.sena.adso.soporteya.model.Area;

public interface AreaRepository extends JpaRepository<Area, Long> {

    List<Area> findAllByOrderByNombreAsc();
}