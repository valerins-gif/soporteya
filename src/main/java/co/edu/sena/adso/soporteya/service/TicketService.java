package co.edu.sena.adso.soporteya.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.adso.soporteya.dto.ConteoEstadoDTO;
import co.edu.sena.adso.soporteya.dto.IndicadorTecnicoDTO;
import co.edu.sena.adso.soporteya.dto.NuevoTicket;
import co.edu.sena.adso.soporteya.dto.TicketResumenDTO;
import co.edu.sena.adso.soporteya.model.Categoria;
import co.edu.sena.adso.soporteya.model.EstadoTicket;
import co.edu.sena.adso.soporteya.model.Rol;
import co.edu.sena.adso.soporteya.model.Ticket;
import co.edu.sena.adso.soporteya.model.Usuario;
import co.edu.sena.adso.soporteya.repository.CategoriaRepository;
import co.edu.sena.adso.soporteya.repository.TicketRepository;
import co.edu.sena.adso.soporteya.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class TicketService {

    private static final int TAMANO_PAGINA = 10;

    private final TicketRepository tickets;
    private final UsuarioRepository usuarios;
    private final CategoriaRepository categorias;

    public TicketService(TicketRepository tickets, UsuarioRepository usuarios, CategoriaRepository categorias) {
        this.tickets = tickets;
        this.usuarios = usuarios;
        this.categorias = categorias;
    }

    // ---------- consultas ----------

    public Page<TicketResumenDTO> misTickets(String email, int pagina) {
        return tickets.findBySolicitanteEmailOrderByFechaCreacionDesc(email, PageRequest.of(pagina, TAMANO_PAGINA))
                .map(TicketResumenDTO::from);
    }

    public List<TicketResumenDTO> pendientes() {
        return tickets.findByEstadoInOrderByFechaLimiteAsc(EstadoTicket.pendientes()).stream()
                .map(TicketResumenDTO::from)
                .toList();
    }

    public List<ConteoEstadoDTO> conteoPorEstado() {
        return tickets.conteoPorEstado();
    }

    public List<IndicadorTecnicoDTO> indicadores(YearMonth mes) {
        LocalDateTime desde = mes.atDay(1).atStartOfDay();
        return tickets.indicadoresEntre(desde, desde.plusMonths(1));
    }

    public List<TicketResumenDTO> vencidos() {
        return tickets.vencidos(EstadoTicket.pendientes(), LocalDateTime.now()).stream()
                .map(TicketResumenDTO::from)
                .toList();
    }

    // ---------- operaciones: cada una es una transacción completa ----------

    @Transactional
    public TicketResumenDTO crear(NuevoTicket datos, String emailSolicitante) {
        Usuario solicitante = usuario(emailSolicitante);
        Categoria categoria = categorias.findById(datos.categoriaId())
                .filter(Categoria::isActiva)
                .orElseThrow(() -> new NegocioException("categoriaId", "La categoría elegida no existe o está inactiva"));

        String asunto = datos.asunto().trim();
        if (tickets.existsBySolicitanteIdAndAsuntoIgnoreCaseAndEstadoIn(solicitante.getId(), asunto, EstadoTicket.pendientes())) {
            throw new NegocioException("asunto", "Ya tiene un ticket pendiente con ese asunto");
        }
        Ticket ticket = new Ticket(asunto, datos.descripcion().trim(), datos.prioridad(), solicitante, categoria);
        return TicketResumenDTO.from(tickets.save(ticket));
    }

    // Un técnico toma un ticket ABIERTO de su especialidad
    @Transactional
    public TicketResumenDTO tomar(Long ticketId, String emailTecnico) {
        Ticket ticket = buscar(ticketId);
        Usuario tecnico = usuario(emailTecnico);
        if (tecnico.getRol() != Rol.TECNICO) {
            throw new NegocioException("Solo un técnico puede tomar tickets");
        }
        if (ticket.getEstado() != EstadoTicket.ABIERTO) {
            throw new NegocioException("El ticket " + ticket.getCodigo() + " ya no está abierto");
        }
        if (!tecnico.atiende(ticket.getCategoria())) {
            throw new NegocioException("No tiene la especialidad " + ticket.getCategoria().getNombre());
        }
        ticket.setTecnico(tecnico);
        transicion(ticket, EstadoTicket.EN_CURSO, tecnico, "Tomado por " + tecnico.getNombreCompleto());
        return TicketResumenDTO.from(ticket);   // sin save(): dirty checking hace el UPDATE
    }

    @Transactional
    public TicketResumenDTO resolver(Long ticketId, String emailTecnico, String solucion) {
        Ticket ticket = buscar(ticketId);
        Usuario tecnico = usuario(emailTecnico);
        if (ticket.getTecnico() == null || !ticket.getTecnico().getId().equals(tecnico.getId())) {
            throw new NegocioException("Solo el técnico asignado puede resolver el ticket");
        }
        if (solucion == null || solucion.trim().length() < 10) {
            throw new NegocioException("texto", "Describa la solución (mínimo 10 caracteres)");
        }
        transicion(ticket, EstadoTicket.RESUELTO, tecnico, "Solución: " + solucion.trim());
        return TicketResumenDTO.from(ticket);
    }

    // El solicitante confirma que quedó bien
    @Transactional
    public TicketResumenDTO cerrar(Long ticketId, String emailSolicitante) {
        Ticket ticket = delSolicitante(ticketId, emailSolicitante);
        transicion(ticket, EstadoTicket.CERRADO, ticket.getSolicitante(), "Confirmado por el solicitante");
        return TicketResumenDTO.from(ticket);
    }

    // El solicitante dice que no quedó bien: vuelve al técnico
    @Transactional
    public TicketResumenDTO reabrir(Long ticketId, String emailSolicitante, String motivo) {
        Ticket ticket = delSolicitante(ticketId, emailSolicitante);
        if (motivo == null || motivo.isBlank()) {
            throw new NegocioException("texto", "Explique por qué reabre el ticket");
        }
        transicion(ticket, EstadoTicket.EN_CURSO, ticket.getSolicitante(), "Reabierto: " + motivo.trim());
        return TicketResumenDTO.from(ticket);
    }

    @Transactional
    public TicketResumenDTO cancelar(Long ticketId, String emailSolicitante) {
        Ticket ticket = delSolicitante(ticketId, emailSolicitante);
        transicion(ticket, EstadoTicket.CANCELADO, ticket.getSolicitante(), "Cancelado por el solicitante");
        return TicketResumenDTO.from(ticket);
    }

    // ---------- ayudas ----------

    private void transicion(Ticket ticket, EstadoTicket nuevo, Usuario autor, String nota) {
        if (!ticket.getEstado().puedePasarA(nuevo)) {
            throw new NegocioException("Un ticket " + ticket.getEstado() + " no puede pasar a " + nuevo);
        }
        ticket.cambiarEstado(nuevo, autor, nota);
    }

    private Ticket delSolicitante(Long ticketId, String email) {
        Ticket ticket = buscar(ticketId);
        if (!ticket.getSolicitante().getEmail().equalsIgnoreCase(email)) {
            throw new NegocioException("Solo quien creó el ticket puede hacer esto");
        }
        return ticket;
    }

    private Ticket buscar(Long id) {
        return tickets.findById(id).orElseThrow(() -> new NegocioException("No existe el ticket " + id));
    }

    private Usuario usuario(String email) {
        return usuarios.findByEmail(email).orElseThrow(() -> new NegocioException("No existe el usuario " + email));
    }
}