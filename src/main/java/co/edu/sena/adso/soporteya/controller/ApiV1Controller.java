package co.edu.sena.adso.soporteya.controller;

import java.time.YearMonth;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.sena.adso.soporteya.dto.Credenciales;
import co.edu.sena.adso.soporteya.dto.IndicadorTecnicoDTO;
import co.edu.sena.adso.soporteya.dto.TicketResumenDTO;
import co.edu.sena.adso.soporteya.service.TicketService;
import co.edu.sena.adso.soporteya.service.TokenService;

// API pública versionada para otras aplicaciones (la pantalla de la oficina de TI, un tablero gerencial…)
@RestController
@RequestMapping("/api/v1")
public class ApiV1Controller {

    private final AuthenticationManager autenticador;
    private final TokenService tokens;
    private final TicketService tickets;

    public ApiV1Controller(AuthenticationManager autenticador, TokenService tokens, TicketService tickets) {
        this.autenticador = autenticador;
        this.tokens = tokens;
        this.tickets = tickets;
    }

    // POST /api/v1/token  {"email": "…", "password": "…"}  →  {"token": "eyJ…", "tipo": "Bearer", …}
    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody Credenciales credenciales) {
        try {
            Authentication autenticado = autenticador.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(credenciales.email(), credenciales.password()));
            return ResponseEntity.ok(tokens.emitir(autenticado));
        } catch (AuthenticationException e) {
            ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problema);
        }
    }

    // GET /api/v1/pendientes  (Authorization: Bearer …) · técnicos y coordinación
    @GetMapping("/pendientes")
    public List<TicketResumenDTO> pendientes() {
        return tickets.pendientes();
    }

    // GET /api/v1/indicadores?mes=2026-12 · solo coordinación
    @GetMapping("/indicadores")
    public List<IndicadorTecnicoDTO> indicadores(@RequestParam(name = "mes", required = false) YearMonth mes) {
        return tickets.indicadores(mes == null ? YearMonth.now() : mes);
    }
}