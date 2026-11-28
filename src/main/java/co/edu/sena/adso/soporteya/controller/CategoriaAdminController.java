package co.edu.sena.adso.soporteya.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.edu.sena.adso.soporteya.dto.CategoriaDTO;
import co.edu.sena.adso.soporteya.dto.CategoriaForm;
import co.edu.sena.adso.soporteya.service.CategoriaService;
import co.edu.sena.adso.soporteya.service.NegocioException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaAdminController {

    private final CategoriaService categorias;

    public CategoriaAdminController(CategoriaService categorias) {
        this.categorias = categorias;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categorias.todas());
        return "admin/categorias";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("categoria", new CategoriaForm());
        return "admin/categoria-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") Long id, Model model) {
        model.addAttribute("categoria", categorias.formulario(id));
        return "admin/categoria-form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("categoria") CategoriaForm categoria, BindingResult resultado,
                          RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return "admin/categoria-form";
        }
        try {
            CategoriaDTO guardada = categorias.guardar(categoria);
            flash.addFlashAttribute("ok", "Categoría «" + guardada.nombre() + "» guardada");
            return "redirect:/admin/categorias";
        } catch (NegocioException e) {
            Formularios.rechazar(resultado, e);
            return "admin/categoria-form";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable("id") Long id, RedirectAttributes flash) {
        CategoriaDTO c = categorias.cambiarEstado(id);
        flash.addFlashAttribute("ok", "«" + c.nombre() + "» quedó " + (c.activa() ? "activa" : "inactiva"));
        return "redirect:/admin/categorias";
    }
}