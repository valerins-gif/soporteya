package co.edu.sena.adso.soporteya.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import co.edu.sena.adso.soporteya.dto.OpcionDTO;
import co.edu.sena.adso.soporteya.dto.TextoForm;
import co.edu.sena.adso.soporteya.dto.TicketForm;
import co.edu.sena.adso.soporteya.dto.TicketResumenDTO;
import co.edu.sena.adso.soporteya.model.Prioridad;
import co.edu.sena.adso.soporteya.service.CategoriaService;
import co.edu.sena.adso.soporteya.service.NegocioException;
import co.edu.sena.adso.soporteya.service.TicketService;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService tickets;
    private final CategoriaService categorias;

    public TicketController(TicketService tickets, CategoriaService categorias) {
        this.tickets = tickets;
        this.categorias = categorias;
    }

    @ModelAttribute("categorias")
    public List<OpcionDTO> categorias() {
        return categorias.opciones();
    }

    @ModelAttribute("prioridades")
    public Prioridad[] prioridades() {
        return Prioridad.values();
    }

    // Mis tickets, por páginas
    @GetMapping
    public String misTickets(@RequestParam(name = "pagina", defaultValue = "0") int pagina,
                             Principal yo, Model model) {
        model.addAttribute("pagina", tickets.misTickets(yo.getName(), pagina));
        return "tickets/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("ticket", new TicketForm());
        return "tickets/nuevo";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("ticket") TicketForm ticket, BindingResult resultado,
                        Principal yo, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return "tickets/nuevo";
        }
        try {
            TicketResumenDTO creado = tickets.crear(ticket.aNuevoTicket(), yo.getName());
            flash.addFlashAttribute("ok", "Ticket " + creado.codigo() + " creado. Fecha límite de atención: "
                    + creado.fechaLimite().toLocalDate());
            return "redirect:/tickets/" + creado.id();
        } catch (NegocioException e) {
            Formularios.rechazar(resultado, e);
            return "tickets/nuevo";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable("id") Long id, Principal yo, Model model) {
        prepararDetalle(id, yo, model);
        return "tickets/detalle";
    }

    @PostMapping("/{id}/comentarios")
    public String comentar(@PathVariable("id") Long id, @Valid @ModelAttribute("comentario") TextoForm form,
                           BindingResult resultado, Principal yo, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                tickets.comentar(id, yo.getName(), form.getTexto(), form.isInterno());
                flash.addFlashAttribute("ok", "Comentario agregado");
                return "redirect:/tickets/" + id;
            } catch (NegocioException e) {
                Formularios.rechazar(resultado, e);
            }
        }
        prepararDetalle(id, yo, model);
        return "tickets/detalle";
    }

    @PostMapping("/{id}/resolver")
    public String resolver(@PathVariable("id") Long id, @Valid @ModelAttribute("resolucion") TextoForm form,
                           BindingResult resultado, Principal yo, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                tickets.resolver(id, yo.getName(), form.getTexto());
                flash.addFlashAttribute("ok", "Ticket resuelto. Queda pendiente la confirmación del solicitante.");
                return "redirect:/tickets/" + id;
            } catch (NegocioException e) {
                Formularios.rechazar(resultado, e);
            }
        }
        prepararDetalle(id, yo, model);
        return "tickets/detalle";
    }

    @PostMapping("/{id}/reabrir")
    public String reabrir(@PathVariable("id") Long id, @Valid @ModelAttribute("reapertura") TextoForm form,
                          BindingResult resultado, Principal yo, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                tickets.reabrir(id, yo.getName(), form.getTexto());
                flash.addFlashAttribute("ok", "Ticket reabierto: vuelve al técnico");
                return "redirect:/tickets/" + id;
            } catch (NegocioException e) {
                Formularios.rechazar(resultado, e);
            }
        }
        prepararDetalle(id, yo, model);
        return "tickets/detalle";
    }

    @PostMapping("/{id}/cerrar")
    public String cerrar(@PathVariable("id") Long id, Principal yo, RedirectAttributes flash) {
        try {
            tickets.cerrar(id, yo.getName());
            flash.addFlashAttribute("ok", "Gracias por confirmar. El ticket quedó cerrado.");
        } catch (NegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tickets/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable("id") Long id, Principal yo, RedirectAttributes flash) {
        try {
            tickets.cancelar(id, yo.getName());
            flash.addFlashAttribute("ok", "Ticket cancelado");
        } catch (NegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tickets/" + id;
    }

    // La página tiene tres formularios; con errores, se conserva el que falló y se crean los demás vacíos
    private void prepararDetalle(Long id, Principal yo, Model model) {
        model.addAttribute("t", tickets.detalle(id, yo.getName()));   // control de acceso horizontal
        for (String nombre : List.of("comentario", "resolucion", "reapertura")) {
            if (!model.containsAttribute(nombre)) {
                model.addAttribute(nombre, new TextoForm());
            }
        }
    }
}