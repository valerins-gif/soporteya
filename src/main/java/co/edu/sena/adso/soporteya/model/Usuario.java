package co.edu.sena.adso.soporteya.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    // Se llena en la Unidad 3 con un hash BCrypt. Nunca la contraseña en texto plano.
    @Column(length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    private boolean activo = true;

    // Muchos usuarios trabajan en un área
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    // Muchos a muchos: un técnico atiende varias categorías y una categoría tiene varios técnicos.
    // Unidireccional: desde Categoria no necesitamos la lista de técnicos.
    @ManyToMany
    @JoinTable(name = "usuario_categoria",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private Set<Categoria> especialidades = new HashSet<>();

    protected Usuario() {
    }

    public Usuario(String nombreCompleto, String email, Rol rol, Area area) {
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.area = area;
    }

    // ¿Este técnico puede atender tickets de esa categoría?
    public boolean atiende(Categoria categoria) {
        return especialidades.stream().anyMatch(c -> c.getId().equals(categoria.getId()));
    }

    public Long getId() {
        return id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Set<Categoria> getEspecialidades() {
        return especialidades;
    }
}