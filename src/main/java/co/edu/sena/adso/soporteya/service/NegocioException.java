package co.edu.sena.adso.soporteya.service;

// Una regla de negocio no se cumplió. Es RuntimeException: si sale de un método
// @Transactional, la transacción se deshace. «campo» (opcional) dice en qué campo
// de un formulario mostrar el error; lo usaremos en la Unidad 2.
public class NegocioException extends RuntimeException {

    private final String campo;

    public NegocioException(String mensaje) {
        this(null, mensaje);
    }

    public NegocioException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}