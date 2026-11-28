package co.edu.sena.adso.soporteya.dto;

import java.time.LocalDateTime;
import java.util.List;

import co.edu.sena.adso.soporteya.model.EstadoTicket;
import co.edu.sena.adso.soporteya.model.Prioridad;
import co.edu.sena.adso.soporteya.model.Ticket;

// Todo lo que muestra la página de un ticket, incluidos sus comentarios
public record TicketDetalleDTO(Long id, String codigo, String asunto, String descripcion, EstadoTicket estado,
                               Prioridad prioridad, String categoria, String solicitante, String solicitanteEmail,
                               String area, String tecnico, String tecnicoEmail, LocalDateTime fechaCreacion,
                               LocalDateTime fechaLimite, LocalDateTime fechaResolucion, boolean vencido,
                               List<ComentarioDTO> comentarios) {

    public static TicketDetalleDTO from(Ticket t, List<ComentarioDTO> comentarios) {
        var s = t.getSolicitante();
        var tec = t.getTecnico();
        return new TicketDetalleDTO(t.getId(), t.getCodigo(), t.getAsunto(), t.getDescripcion(), t.getEstado(),
                t.getPrioridad(), t.getCategoria().getNombre(), s.getNombreCompleto(), s.getEmail(),
                s.getArea() == null ? "—" : s.getArea().getNombre(),
                tec == null ? null : tec.getNombreCompleto(), tec == null ? null : tec.getEmail(),
                t.getFechaCreacion(), t.getFechaLimite(), t.getFechaResolucion(), t.isVencido(), comentarios);
    }
}