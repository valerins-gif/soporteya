package co.edu.sena.adso.soporteya.dto;

// Lo que llega en el cuerpo de POST /api/v1/token
public record Credenciales(String email, String password) {
}