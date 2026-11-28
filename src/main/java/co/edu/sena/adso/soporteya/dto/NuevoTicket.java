package co.edu.sena.adso.soporteya.dto;

import co.edu.sena.adso.soporteya.model.Prioridad;

// Los datos que se necesitan para crear un ticket
public record NuevoTicket(String asunto, String descripcion, Prioridad prioridad, Long categoriaId) {
}