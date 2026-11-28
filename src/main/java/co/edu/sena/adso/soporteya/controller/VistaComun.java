package co.edu.sena.adso.soporteya.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import co.edu.sena.adso.soporteya.dto.UsuarioDTO;
import co.edu.sena.adso.soporteya.service.UsuarioService;

// Agrega a TODAS las vistas el usuario que inició sesión («yo»). Si nadie ha iniciado sesión, es null.
@ControllerAdvice(annotations = Controller.class)
public class VistaComun {

    private final UsuarioService usuarios;

    public VistaComun(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @ModelAttribute("yo")
    public UsuarioDTO yo(Authentication autenticacion) {
        if (autenticacion == null || autenticacion instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return usuarios.porEmail(autenticacion.getName());
    }
}