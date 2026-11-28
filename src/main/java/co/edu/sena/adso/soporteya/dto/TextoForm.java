package co.edu.sena.adso.soporteya.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Un formulario de un solo texto: comentario, solución o motivo para reabrir
public class TextoForm {

    @NotBlank(message = "Escriba el texto")
    @Size(max = 2000, message = "Máximo 2000 caracteres")
    private String texto;

    // Solo para comentarios de técnicos y coordinación
    private boolean interno;

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public boolean isInterno() {
        return interno;
    }

    public void setInterno(boolean interno) {
        this.interno = interno;
    }
}