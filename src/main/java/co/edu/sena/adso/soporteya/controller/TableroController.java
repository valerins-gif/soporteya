package co.edu.sena.adso.soporteya.controller;

import java.security.Principal;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.edu.sena.adso.soporteya.dto.TicketResumenDTO;
import co.edu.sena.adso.soporteya.service.NegocioException;
import co.edu.sena.adso.soporteya.service.TicketService;

// El tablero de la mesa de ayuda: los técnicos toman tickets desde aquí
@Controller
public class TableroController {

    private final TicketService tickets;

    public TableroController(TicketService tickets) {
        this.tickets = tickets;
    }

    @GetMapping("/tablero")
    public String tablero(Model model) {
        model.addAttribute("tablero", tickets.tablero());
        return "tablero";
    }

    @PostMapping("/tablero/{id}/tomar")
    public String tomar(@PathVariable("id") Long id, Principal yo, RedirectAttributes flash) {
        try {
            TicketResumenDTO t = tickets.tomar(id, yo.getName());
            flash.addFlashAttribute("ok", "Usted tomó el ticket " + t.codigo());
        } catch (NegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        } catch (ObjectOptimisticLockingFailureException e) {
            // @Version: otro técnico lo tomó en el mismo instante
            flash.addFlashAttribute("error", "Otro técnico tomó ese ticket al mismo tiempo. Elija otro.");
        }
        return "redirect:/tablero";
    }
}