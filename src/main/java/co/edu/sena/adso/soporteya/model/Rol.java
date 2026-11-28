package co.edu.sena.adso.soporteya.model;

public enum Rol {
    SOLICITANTE("Solicitante"),   // reporta fallas y sigue sus tickets
    TECNICO("Técnico"),           // toma y resuelve tickets de sus especialidades
    COORDINADOR("Coordinador");   // administra categorías y usuarios, y mide los tiempos

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}