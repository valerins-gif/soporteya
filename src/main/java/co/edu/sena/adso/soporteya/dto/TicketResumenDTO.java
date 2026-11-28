package co.edu.sena.adso.soporteya.dto;

import java.time.LocalDateTime;

import co.edu.sena.adso.soporteya.model.EstadoTicket;
import co.edu.sena.adso.soporteya.model.Prioridad;
import co.edu.sena.adso.soporteya.model.Ticket;

// Lo que muestran las listas y el tablero. Nunca se entrega la entidad a la vista.
public record TicketResumenDTO(Long id, String codigo, String asunto, EstadoTicket estado, Prioridad prioridad,
                               String categoria, String solicitante, String tecnico,
                               LocalDateTime fechaCreacion, LocalDateTime fechaLimite, boolean vencido) {

    public static TicketResumenDTO from(Ticket t) {
        return new TicketResumenDTO(t.getId(), t.getCodigo(), t.getAsunto(), t.getEstado(), t.getPrioridad(),
                t.getCategoria().getNombre(), t.getSolicitante().getNombreCompleto(),
                t.getTecnico() == null ? null : t.getTecnico().getNombreCompleto(),
                t.getFechaCreacion(), t.getFechaLimite(), t.isVencido());
    }
}