package co.edu.sena.adso.soporteya.model;

import java.util.Set;

// El ciclo de vida de un ticket. Cada estado sabe a cuáles puede pasar.
//
//  ABIERTO ──tomar──▶ EN_CURSO ──resolver──▶ RESUELTO ──cerrar──▶ CERRADO
//     │                   ▲                      │
//     └──cancelar──▶ CANCELADO                   └──reabrir──┘
public enum EstadoTicket {
    ABIERTO("Abierto"),
    EN_CURSO("En curso"),
    RESUELTO("Resuelto"),
    CERRADO("Cerrado"),
    CANCELADO("Cancelado");

    private final String etiqueta;   // el texto que ve el usuario

    EstadoTicket(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean puedePasarA(EstadoTicket nuevo) {
        return switch (this) {
            case ABIERTO -> nuevo == EN_CURSO || nuevo == CANCELADO;
            case EN_CURSO -> nuevo == RESUELTO;
            case RESUELTO -> nuevo == CERRADO || nuevo == EN_CURSO;
            case CERRADO, CANCELADO -> false;
        };
    }

    // Estados en los que el ticket sigue pendiente para la mesa de ayuda
    public static Set<EstadoTicket> pendientes() {
        return Set.of(ABIERTO, EN_CURSO);
    }

    public boolean esFinal() {
        return this == CERRADO || this == CANCELADO;
    }
}