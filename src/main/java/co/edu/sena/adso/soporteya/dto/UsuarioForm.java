package co.edu.sena.adso.soporteya.dto;

import java.util.HashSet;
import java.util.Set;

import co.edu.sena.adso.soporteya.model.Categoria;
import co.edu.sena.adso.soporteya.model.Rol;
import co.edu.sena.adso.soporteya.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Formulario de usuarios. La relación muchos a muchos llega como un conjunto de ids de las casillas marcadas.
public class UsuarioForm {

    private Long id;

    @NotBlank(message = "Escriba el nombre completo")
    @Size(max = 120, message = "Máximo 120 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "Escriba el correo")
    @Email(message = "Escriba un correo válido")
    private String email;

    @NotNull(message = "Elija el rol")
    private Rol rol = Rol.SOLICITANTE;

    @NotNull(message = "Elija el área")
    private Long areaId;

    private Set<Long> especialidades = new HashSet<>();

    // Unidad 3 · obligatoria al crear; si se deja vacía al editar, se conserva la actual
    @Size(max = 72, message = "Máximo 72 caracteres")
    private String password;

    public static UsuarioForm desde(Usuario u) {
        UsuarioForm f = new UsuarioForm();
        f.id = u.getId();
        f.nombreCompleto = u.getNombreCompleto();
        f.email = u.getEmail();
        f.rol = u.getRol();
        f.areaId = u.getArea() == null ? null : u.getArea().getId();
        u.getEspecialidades().stream().map(Categoria::getId).forEach(f.especialidades::add);
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

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Long> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(Set<Long> especialidades) {
        this.especialidades = especialidades;
    }
}