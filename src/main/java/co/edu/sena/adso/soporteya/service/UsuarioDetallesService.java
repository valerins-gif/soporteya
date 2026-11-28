package co.edu.sena.adso.soporteya.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.adso.soporteya.model.Usuario;
import co.edu.sena.adso.soporteya.repository.UsuarioRepository;

// Spring Security llama a este servicio en cada inicio de sesión para buscar al usuario
@Service
public class UsuarioDetallesService implements UserDetailsService {

    private final UsuarioRepository usuarios;

    public UsuarioDetallesService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarios.findByEmail(email.trim().toLowerCase())
                .filter(u -> u.getPassword() != null)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o contraseña incorrectos"));
        return User.withUsername(usuario.getEmail())
                .password(usuario.getPassword())          // el hash BCrypt guardado
                .roles(usuario.getRol().name())           // ROLE_SOLICITANTE, ROLE_TECNICO…
                .disabled(!usuario.isActivo())            // un usuario desactivado no entra
                .build();
    }
}