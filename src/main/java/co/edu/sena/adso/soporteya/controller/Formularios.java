package co.edu.sena.adso.soporteya.controller;

import org.springframework.validation.BindingResult;

import co.edu.sena.adso.soporteya.service.NegocioException;

// Muestra un error de negocio dentro del formulario, igual que uno de formato
final class Formularios {

    private Formularios() {
    }

    static void rechazar(BindingResult resultado, NegocioException e) {
        if (e.getCampo() != null) {
            resultado.rejectValue(e.getCampo(), "negocio", e.getMessage());   // junto al campo
        } else {
            resultado.reject("negocio", e.getMessage());                       // arriba del formulario
        }
    }
}