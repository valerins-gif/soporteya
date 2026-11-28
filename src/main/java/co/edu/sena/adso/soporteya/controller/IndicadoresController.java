package co.edu.sena.adso.soporteya.controller;

import java.time.YearMonth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import co.edu.sena.adso.soporteya.service.TicketService;

// Indicadores para la coordinación: estados, cumplimiento por técnico y vencidos
@Controller
public class IndicadoresController {

    private final TicketService tickets;

    public IndicadoresController(TicketService tickets) {
        this.tickets = tickets;
    }

    // GET /admin/indicadores?mes=2026-12
    @GetMapping("/admin/indicadores")
    public String indicadores(@RequestParam(name = "mes", required = false) YearMonth mes, Model model) {
        YearMonth elegido = mes == null ? YearMonth.now() : mes;
        model.addAttribute("mes", elegido);
        model.addAttribute("conteos", tickets.conteoPorEstado());
        model.addAttribute("indicadores", tickets.indicadores(elegido));
        model.addAttribute("vencidos", tickets.vencidos());
        return "admin/indicadores";
    }
}