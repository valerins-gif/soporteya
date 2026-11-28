package co.edu.sena.adso.soporteya.dto;

// Lo arma la consulta JPQL TicketRepository.indicadoresEntre
public record IndicadorTecnicoDTO(String tecnico, Long resueltos, Long fueraDeTiempo) {

    public long aTiempo() {
        return resueltos - fueraDeTiempo;
    }

    // Porcentaje de cumplimiento del acuerdo de nivel de servicio (ANS)
    public int cumplimiento() {
        return resueltos == 0 ? 100 : (int) (aTiempo() * 100 / resueltos);
    }
}