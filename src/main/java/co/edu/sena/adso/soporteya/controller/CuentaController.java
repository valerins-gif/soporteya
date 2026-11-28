package co.edu.sena.adso.soporteya.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.edu.sena.adso.soporteya.dto.RegistroForm;
import co.edu.sena.adso.soporteya.service.NegocioException;
import co.edu.sena.adso.soporteya.service.UsuarioService;
import jakarta.validation.Valid;

// Iniciar sesión, registrarse y la página de acceso denegado
@Controller
public class CuentaController {

    private final UsuarioService usuarios;

    public CuentaController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    // El POST /login lo atiende Spring Security; aquí solo mostramos la página
    @GetMapping("/login")
    public String login() {
        return "cuenta/login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("registro", new RegistroForm());
        model.addAttribute("areas", usuarios.areas());
        return "cuenta/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("registro") RegistroForm registro, BindingResult resultado,
                            Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                usuarios.registrar(registro);
                flash.addFlashAttribute("ok", "Cuenta creada. Ya puede iniciar sesión.");
                return "redirect:/login";
            } catch (NegocioException e) {
                Formularios.rechazar(resultado, e);
            }
        }
        model.addAttribute("areas", usuarios.areas());
        return "cuenta/registro";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "cuenta/acceso-denegado";
    }
}