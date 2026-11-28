package co.edu.sena.adso.soporteya.dto;

import java.util.stream.Collectors;

import co.edu.sena.adso.soporteya.model.Categoria;
import co.edu.sena.adso.soporteya.model.Rol;
import co.edu.sena.adso.soporteya.model.Usuario;

public record UsuarioDTO(Long id, String nombreCompleto, String email, Rol rol, String area,
                         String especialidades, boolean activo) {

    public static UsuarioDTO from(Usuario u) {
        String esp = u.getEspecialidades().stream().map(Categoria::getNombre).sorted()
                .collect(Collectors.joining(", "));
        return new UsuarioDTO(u.getId(), u.getNombreCompleto(), u.getEmail(), u.getRol(),
                u.getArea() == null ? "—" : u.getArea().getNombre(), esp, u.isActivo());
    }
}