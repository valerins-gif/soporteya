package co.edu.sena.adso.soporteya.model;

// Cada prioridad trae su tiempo máximo de solución (acuerdo de nivel de servicio, ANS)
public enum Prioridad {
    BAJA("Baja", 72),
    MEDIA("Media", 24),
    ALTA("Alta", 8),
    CRITICA("Crítica", 4);

    private final String etiqueta;
    private final int horasLimite;

    Prioridad(String etiqueta, int horasLimite) {
        this.etiqueta = etiqueta;
        this.horasLimite = horasLimite;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public int getHorasLimite() {
        return horasLimite;
    }
}