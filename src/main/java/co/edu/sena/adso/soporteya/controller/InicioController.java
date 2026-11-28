package co.edu.sena.adso.soporteya.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import co.edu.sena.adso.soporteya.service.TicketService;

@Controller
public class InicioController {

    private final TicketService tickets;

    public InicioController(TicketService tickets) {
        this.tickets = tickets;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("conteos", tickets.conteoPorEstado());
        return "index";
    }
}