package co.edu.sena.adso.soporteya.config;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;

// SOLO PARA LA UNIDAD 2. Todavía no hay login, así que el menú deja elegir
// «quién soy» y lo guardamos en la sesión. Cualquiera puede hacerse pasar por otro:
// ese es justamente el problema que resuelve la Unidad 3. Allí esta clase se borra.
@Component
public class SesionDemo {

    private static final String CLAVE = "emailDemo";
    private static final String POR_DEFECTO = "ana.perez@soporteya.co";

    public String email(HttpSession sesion) {
        Object email = sesion.getAttribute(CLAVE);
        return email == null ? POR_DEFECTO : email.toString();
    }

    public void cambiar(HttpSession sesion, String email) {
        sesion.setAttribute(CLAVE, email);
    }
}