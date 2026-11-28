package co.edu.sena.adso.soporteya.dto;

import co.edu.sena.adso.soporteya.model.Categoria;

public record CategoriaDTO(Long id, String nombre, String descripcion, boolean activa) {

    public static CategoriaDTO from(Categoria c) {
        return new CategoriaDTO(c.getId(), c.getNombre(), c.getDescripcion(), c.isActiva());
    }
}