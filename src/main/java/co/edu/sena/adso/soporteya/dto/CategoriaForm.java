package co.edu.sena.adso.soporteya.dto;

import co.edu.sena.adso.soporteya.model.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaForm {

    private Long id;

    @NotBlank(message = "Escriba el nombre")
    @Size(max = 60, message = "Máximo 60 caracteres")
    private String nombre;

    @Size(max = 250, message = "Máximo 250 caracteres")
    private String descripcion;

    public static CategoriaForm desde(Categoria c) {
        CategoriaForm f = new CategoriaForm();
        f.id = c.getId();
        f.nombre = c.getNombre();
        f.descripcion = c.getDescripcion();
        return f;
    }

    public boolean esNuevo() {
        return id == null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}