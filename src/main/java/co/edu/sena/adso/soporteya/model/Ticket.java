package co.edu.sena.adso.soporteya.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Version;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String asunto;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTicket estado = EstadoTicket.ABIERTO;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaLimite;

    private LocalDateTime fechaResolucion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_id")
    private Usuario solicitante;

    // Opcional: un ticket ABIERTO todavía no tiene técnico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Composición: un comentario no existe sin su ticket
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fecha ASC")
    private List<Comentario> comentarios = new ArrayList<>();

    // Bloqueo optimista: si dos técnicos toman el mismo ticket a la vez, el segundo falla
    @Version
    private Long version;

    protected Ticket() {
    }

    // Un ticket nuevo calcula su fecha límite según la prioridad
    public Ticket(String asunto, String descripcion, Prioridad prioridad, Usuario solicitante, Categoria categoria) {
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.solicitante = solicitante;
        this.categoria = categoria;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaLimite = fechaCreacion.plusHours(prioridad.getHorasLimite());
    }

    // Código para mostrar al usuario: SY-00042
    public String getCodigo() {
        return id == null ? "SY-nuevo" : String.format("SY-%05d", id);
    }

    public boolean isVencido() {
        return !estado.esFinal() && estado != EstadoTicket.RESUELTO && LocalDateTime.now().isAfter(fechaLimite);
    }

    // Toda transición pasa por aquí: valida el cambio y deja constancia en los comentarios
    public void cambiarEstado(EstadoTicket nuevo, Usuario autor, String nota) {
        if (!estado.puedePasarA(nuevo)) {
            throw new IllegalStateException("Un ticket " + estado + " no puede pasar a " + nuevo);
        }
        String texto = "Estado: " + estado + " → " + nuevo + (nota == null || nota.isBlank() ? "" : ". " + nota);
        this.estado = nuevo;
        if (nuevo == EstadoTicket.RESUELTO) {
            fechaResolucion = LocalDateTime.now();
        } else if (nuevo == EstadoTicket.EN_CURSO) {
            fechaResolucion = null;     // si se reabre, se borra la resolución anterior
        }
        agregarComentario(new Comentario(texto, autor, false));
    }

    // Sincroniza los dos lados de la relación
    public void agregarComentario(Comentario comentario) {
        comentarios.add(comentario);
        comentario.setTicket(this);
    }

    public Long getId() {
        return id;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public EstadoTicket getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public void setTecnico(Usuario tecnico) {
        this.tecnico = tecnico;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }
}