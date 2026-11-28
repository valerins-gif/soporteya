package co.edu.sena.adso.soporteya.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

import co.edu.sena.adso.soporteya.config.SesionDemo;
import co.edu.sena.adso.soporteya.dto.UsuarioDTO;
import co.edu.sena.adso.soporteya.service.UsuarioService;

// Agrega a TODAS las vistas quién es el usuario actual («yo»), para el menú y los permisos
@ControllerAdvice(annotations = Controller.class)
public class VistaComun {

    private final UsuarioService usuarios;
    private final SesionDemo demo;

    public VistaComun(UsuarioService usuarios, SesionDemo demo) {
        this.usuarios = usuarios;
        this.demo = demo;
    }

    @ModelAttribute("yo")
    public UsuarioDTO yo(HttpSession sesion) {
        return usuarios.porEmail(demo.email(sesion));
    }

    // Solo Unidad 2: la lista del selector «Actuar como»
    @ModelAttribute("usuariosDemo")
    public List<UsuarioDTO> usuariosDemo() {
        return usuarios.todos();
    }
}