package co.edu.sena.adso.soporteya.dto;

import java.util.List;

// Las tres columnas del tablero de la mesa de ayuda
public record TableroDTO(List<TicketResumenDTO> abiertos, List<TicketResumenDTO> enCurso,
                         List<TicketResumenDTO> resueltos) {
}