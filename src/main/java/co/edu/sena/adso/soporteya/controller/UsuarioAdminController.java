package co.edu.sena.adso.soporteya.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.edu.sena.adso.soporteya.dto.OpcionDTO;
import co.edu.sena.adso.soporteya.dto.UsuarioDTO;
import co.edu.sena.adso.soporteya.dto.UsuarioForm;
import co.edu.sena.adso.soporteya.model.Rol;
import co.edu.sena.adso.soporteya.service.CategoriaService;
import co.edu.sena.adso.soporteya.service.NegocioException;
import co.edu.sena.adso.soporteya.service.UsuarioService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioService usuarios;
    private final CategoriaService categorias;

    public UsuarioAdminController(UsuarioService usuarios, CategoriaService categorias) {
        this.usuarios = usuarios;
        this.categorias = categorias;
    }

    @ModelAttribute("areas")
    public List<OpcionDTO> areas() {
        return usuarios.areas();
    }

    @ModelAttribute("categorias")
    public List<OpcionDTO> categorias() {
        return categorias.opciones();
    }

    @ModelAttribute("roles")
    public Rol[] roles() {
        return Rol.values();
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarios.todos());
        return "admin/usuarios";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new UsuarioForm());
        return "admin/usuario-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") Long id, Model model) {
        model.addAttribute("usuario", usuarios.formulario(id));
        return "admin/usuario-form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("usuario") UsuarioForm usuario, BindingResult resultado,
                          RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return "admin/usuario-form";
        }
        try {
            UsuarioDTO guardado = usuarios.guardar(usuario);
            flash.addFlashAttribute("ok", "Usuario «" + guardado.nombreCompleto() + "» guardado");
            return "redirect:/admin/usuarios";
        } catch (NegocioException e) {
            Formularios.rechazar(resultado, e);
            return "admin/usuario-form";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable("id") Long id, RedirectAttributes flash) {
        UsuarioDTO u = usuarios.cambiarEstado(id);
        flash.addFlashAttribute("ok", "«" + u.nombreCompleto() + "» quedó " + (u.activo() ? "activo" : "inactivo"));
        return "redirect:/admin/usuarios";
    }
}