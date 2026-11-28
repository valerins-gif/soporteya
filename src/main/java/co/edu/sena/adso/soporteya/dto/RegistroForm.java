package co.edu.sena.adso.soporteya.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Registro de un funcionario como solicitante
public class RegistroForm {

    @NotBlank(message = "Escriba su nombre completo")
    @Size(max = 120, message = "Máximo 120 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "Escriba su correo")
    @Email(message = "Escriba un correo válido")
    private String email;

    @NotNull(message = "Elija su área")
    private Long areaId;

    @NotBlank(message = "Escriba una contraseña")
    @Size(min = 8, max = 72, message = "Entre 8 y 72 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$", message = "Debe tener al menos una letra y un número")
    private String password;

    @NotBlank(message = "Repita la contraseña")
    private String confirmacion;

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmacion() {
        return confirmacion;
    }

    public void setConfirmacion(String confirmacion) {
        this.confirmacion = confirmacion;
    }
}