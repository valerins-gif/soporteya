package co.edu.sena.adso.soporteya.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.adso.soporteya.dto.OpcionDTO;
import co.edu.sena.adso.soporteya.dto.RegistroForm;
import co.edu.sena.adso.soporteya.dto.UsuarioDTO;
import co.edu.sena.adso.soporteya.dto.UsuarioForm;
import co.edu.sena.adso.soporteya.model.Area;
import co.edu.sena.adso.soporteya.model.Categoria;
import co.edu.sena.adso.soporteya.model.Rol;
import co.edu.sena.adso.soporteya.model.Usuario;
import co.edu.sena.adso.soporteya.repository.AreaRepository;
import co.edu.sena.adso.soporteya.repository.CategoriaRepository;
import co.edu.sena.adso.soporteya.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final AreaRepository areas;
    private final CategoriaRepository categorias;
    private final PasswordEncoder encoder;
    private final String dominio;

    public UsuarioService(UsuarioRepository usuarios, AreaRepository areas, CategoriaRepository categorias,
                          PasswordEncoder encoder, @Value("${soporteya.dominio-correo}") String dominio) {
        this.usuarios = usuarios;
        this.areas = areas;
        this.categorias = categorias;
        this.encoder = encoder;
        this.dominio = dominio;
    }

    // Unidad 3 · cualquier funcionario puede registrarse como SOLICITANTE, solo con el correo de la empresa
    @Transactional
    public UsuarioDTO registrar(RegistroForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (!email.endsWith("@" + dominio)) {
            throw new NegocioException("email", "Use su correo de la empresa (@" + dominio + ")");
        }
        if (usuarios.existsByEmailIgnoreCase(email)) {
            throw new NegocioException("email", "Ya hay una cuenta con ese correo");
        }
        if (!form.getPassword().equals(form.getConfirmacion())) {
            throw new NegocioException("confirmacion", "Las contraseñas no coinciden");
        }
        Area area = areas.findById(form.getAreaId())
                .orElseThrow(() -> new NegocioException("areaId", "El área elegida no existe"));
        Usuario nuevo = new Usuario(form.getNombreCompleto().trim(), email, Rol.SOLICITANTE, area);
        nuevo.setPassword(encoder.encode(form.getPassword()));     // nunca en texto plano
        return UsuarioDTO.from(usuarios.save(nuevo));
    }

    public UsuarioDTO porEmail(String email) {
        return usuarios.findByEmail(email).map(UsuarioDTO::from)
                .orElseThrow(() -> new NegocioException("No existe el usuario " + email));
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    public List<UsuarioDTO> todos() {
        return usuarios.findAllByOrderByRolAscNombreCompletoAsc().stream().map(UsuarioDTO::from).toList();
    }

    public List<OpcionDTO> areas() {
        return areas.findAllByOrderByNombreAsc().stream().map(a -> new OpcionDTO(a.getId(), a.getNombre())).toList();
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    public UsuarioForm formulario(Long id) {
        return UsuarioForm.desde(buscar(id));
    }

    @Transactional
    @PreAuthorize("hasRole('COORDINADOR')")
    public UsuarioDTO guardar(UsuarioForm form) {
        String email = form.getEmail().trim().toLowerCase();
        Usuario usuario = form.esNuevo() ? null : buscar(form.getId());
        boolean emailCambio = usuario == null || !usuario.getEmail().equalsIgnoreCase(email);
        if (emailCambio && usuarios.existsByEmailIgnoreCase(email)) {
            throw new NegocioException("email", "Ya hay un usuario con el correo " + email);
        }
        // Regla: un técnico necesita al menos una especialidad
        if (form.getRol() == Rol.TECNICO && form.getEspecialidades().isEmpty()) {
            throw new NegocioException("especialidades", "Marque al menos una especialidad para el técnico");
        }
        Area area = areas.findById(form.getAreaId())
                .orElseThrow(() -> new NegocioException("areaId", "El área elegida no existe"));

        boolean sinPassword = form.getPassword() == null || form.getPassword().isBlank();
        if (usuario == null && sinPassword) {
            throw new NegocioException("password", "Asigne una contraseña inicial");
        }
        if (!sinPassword && form.getPassword().length() < 8) {
            throw new NegocioException("password", "Mínimo 8 caracteres");
        }
        if (usuario == null) {
            usuario = new Usuario(form.getNombreCompleto().trim(), email, form.getRol(), area);
        } else {
            usuario.setNombreCompleto(form.getNombreCompleto().trim());
            usuario.setEmail(email);
            usuario.setRol(form.getRol());
            usuario.setArea(area);
        }
        if (!sinPassword) {
            usuario.setPassword(encoder.encode(form.getPassword()));
        }
        // Muchos a muchos: se reemplaza el conjunto por las categorías marcadas
        usuario.getEspecialidades().clear();
        if (form.getRol() == Rol.TECNICO) {
            usuario.getEspecialidades().addAll(new HashSet<Categoria>(categorias.findAllById(form.getEspecialidades())));
        }
        return UsuarioDTO.from(usuarios.save(usuario));
    }

    @Transactional
    @PreAuthorize("hasRole('COORDINADOR')")
    public UsuarioDTO cambiarEstado(Long id) {
        Usuario usuario = buscar(id);
        usuario.setActivo(!usuario.isActivo());
        return UsuarioDTO.from(usuario);
    }

    private Usuario buscar(Long id) {
        return usuarios.findById(id).orElseThrow(() -> new NegocioException("No existe el usuario " + id));
    }
}