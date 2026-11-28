package co.edu.sena.adso.soporteya.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.sena.adso.soporteya.dto.TableroDTO;
import co.edu.sena.adso.soporteya.dto.TicketResumenDTO;
import co.edu.sena.adso.soporteya.service.TicketService;

// JSON para el JavaScript de NUESTRAS páginas. Usa la sesión del navegador y exige el código CSRF en los POST.
@RestController
@RequestMapping("/api/web")
public class WebApiController {

    private final TicketService tickets;

    public WebApiController(TicketService tickets) {
        this.tickets = tickets;
    }

    // GET /api/web/tablero → las tres columnas en JSON
    @GetMapping("/tablero")
    public TableroDTO tablero() {
        return tickets.tablero();
    }

    // POST /api/web/tablero/5/tomar → el ticket tomado, o 409 con el motivo
    @PostMapping("/tablero/{id}/tomar")
    public TicketResumenDTO tomar(@PathVariable("id") Long id, Principal yo) {
        return tickets.tomar(id, yo.getName());
    }

    // GET /api/web/mis-pendientes → {"pendientes": 2}
    @GetMapping("/mis-pendientes")
    public Map<String, Long> misPendientes(Principal yo) {
        return Map.of("pendientes", tickets.contarMisPendientes(yo.getName()));
    }
}