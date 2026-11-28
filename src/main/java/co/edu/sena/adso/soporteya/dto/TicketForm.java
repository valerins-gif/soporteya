package co.edu.sena.adso.soporteya.dto;

import co.edu.sena.adso.soporteya.model.Prioridad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Formulario «Nuevo ticket». Reglas de FORMATO; las de negocio están en TicketService.
public class TicketForm {

    @NotBlank(message = "Escriba el asunto")
    @Size(min = 5, max = 120, message = "El asunto debe tener entre 5 y 120 caracteres")
    private String asunto;

    @NotBlank(message = "Describa la falla")
    @Size(min = 20, max = 2000, message = "Describa la falla con al menos 20 caracteres")
    private String descripcion;

    @NotNull(message = "Elija la prioridad")
    private Prioridad prioridad = Prioridad.MEDIA;

    @NotNull(message = "Elija la categoría")
    private Long categoriaId;

    public NuevoTicket aNuevoTicket() {
        return new NuevoTicket(asunto, descripcion, prioridad, categoriaId);
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}