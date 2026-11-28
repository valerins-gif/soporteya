package co.edu.sena.adso.soporteya.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import co.edu.sena.adso.soporteya.service.NegocioException;

// Errores de la API como JSON (ProblemDetail, RFC 9457). Solo para los @RestController.
@RestControllerAdvice(annotations = RestController.class)
public class ManejadorErrores {

    @ExceptionHandler(NegocioException.class)
    public ProblemDetail negocio(NegocioException e) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problema.setTitle("Regla de negocio");
        return problema;
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail concurrencia(ObjectOptimisticLockingFailureException e) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Otro técnico tomó ese ticket al mismo tiempo. Elija otro.");
        problema.setTitle("Cambio simultáneo");
        return problema;
    }
}