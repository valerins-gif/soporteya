package co.edu.sena.adso.soporteya.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.sena.adso.soporteya.dto.ConteoEstadoDTO;
import co.edu.sena.adso.soporteya.dto.IndicadorTecnicoDTO;
import co.edu.sena.adso.soporteya.model.EstadoTicket;
import co.edu.sena.adso.soporteya.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // «Mis tickets» del solicitante, por páginas. @EntityGraph evita N+1 al mostrar categoría y técnico.
    @EntityGraph(attributePaths = {"categoria", "tecnico", "solicitante"})
    Page<Ticket> findBySolicitanteEmailOrderByFechaCreacionDesc(String email, Pageable pageable);

    // Tablero de la mesa de ayuda: los más urgentes (fecha límite más cercana) primero
    @EntityGraph(attributePaths = {"categoria", "tecnico", "solicitante"})
    List<Ticket> findByEstadoInOrderByFechaLimiteAsc(Collection<EstadoTicket> estados);

    // Regla de negocio: no repetir un ticket pendiente con el mismo asunto
    boolean existsBySolicitanteIdAndAsuntoIgnoreCaseAndEstadoIn(Long solicitanteId, String asunto,
                                                               Collection<EstadoTicket> estados);

    long countBySolicitanteEmailAndEstadoIn(String email, Collection<EstadoTicket> estados);

    // Proyección: cuántos tickets hay en cada estado
    @Query("""
            select new co.edu.sena.adso.soporteya.dto.ConteoEstadoDTO(t.estado, count(t))
            from Ticket t
            group by t.estado
            order by t.estado
            """)
    List<ConteoEstadoDTO> conteoPorEstado();

    // Indicadores por técnico: resueltos y cuántos se resolvieron después de la fecha límite
    @Query("""
            select new co.edu.sena.adso.soporteya.dto.IndicadorTecnicoDTO(
                u.nombreCompleto,
                count(t),
                sum(case when t.fechaResolucion > t.fechaLimite then 1 else 0 end))
            from Ticket t
            join t.tecnico u
            where t.fechaResolucion >= :desde and t.fechaResolucion < :hasta
            group by u.nombreCompleto
            order by count(t) desc
            """)
    List<IndicadorTecnicoDTO> indicadoresEntre(@Param("desde") LocalDateTime desde,
                                               @Param("hasta") LocalDateTime hasta);

    // Tickets pendientes que ya pasaron su fecha límite. join fetch: todo en una consulta.
    @Query("""
            select t from Ticket t
            join fetch t.categoria
            join fetch t.solicitante
            left join fetch t.tecnico
            where t.estado in :estados and t.fechaLimite < :ahora
            order by t.fechaLimite
            """)
    List<Ticket> vencidos(@Param("estados") Collection<EstadoTicket> estados, @Param("ahora") LocalDateTime ahora);
}