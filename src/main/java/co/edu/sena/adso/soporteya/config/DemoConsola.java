package co.edu.sena.adso.soporteya.config;

import java.time.YearMonth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.edu.sena.adso.soporteya.dto.NuevoTicket;
import co.edu.sena.adso.soporteya.model.Prioridad;
import co.edu.sena.adso.soporteya.service.NegocioException;
import co.edu.sena.adso.soporteya.service.TicketService;

// Recorre las reglas de la Unidad 1 en la consola. Solo corre con el perfil «demo»:
//   ./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
// Cambia los datos: para repetirla, vuelva a crear la base de datos.
@Component
@Profile("demo")
@Order(2)
public class DemoConsola implements CommandLineRunner {

    private final TicketService servicio;

    public DemoConsola(TicketService servicio) {
        this.servicio = servicio;
    }

    @Override
    public void run(String... args) {
        titulo("1. Tickets por estado (proyección con @Query)");
        servicio.conteoPorEstado().forEach(c -> System.out.println(c.estado() + ": " + c.cantidad()));

        titulo("2. Tablero: pendientes, los más urgentes primero (@EntityGraph)");
        servicio.pendientes().forEach(t -> System.out.printf("%s · %-8s · %-9s · %s%n",
                t.codigo(), t.prioridad(), t.estado(), t.asunto()));

        titulo("3. Jorge crea un ticket");
        var nuevo = servicio.crear(new NuevoTicket("La pantalla parpadea",
                "El monitor parpadea cada pocos minutos.", Prioridad.MEDIA, 1L), "jorge.herrera@soporteya.co");
        System.out.println("Creado " + nuevo.codigo() + " con fecha límite " + nuevo.fechaLimite());

        titulo("4. Reglas de negocio que deben fallar");
        intentar("Jorge repite el asunto", () -> servicio.crear(new NuevoTicket("la pantalla parpadea",
                "Otra vez.", Prioridad.BAJA, 1L), "jorge.herrera@soporteya.co"));
        intentar("Diana toma un ticket de Impresoras", () -> servicio.tomar(3L, "diana.torres@soporteya.co"));
        intentar("Ana (solicitante) toma un ticket", () -> servicio.tomar(1L, "ana.perez@soporteya.co"));
        intentar("Paola cierra un ticket de Ana", () -> servicio.cerrar(2L, "paola.martinez@soporteya.co"));

        titulo("5. Ciclo completo del ticket SY-00001");
        servicio.tomar(1L, "carlos.ruiz@soporteya.co");
        System.out.println("Carlos lo toma → EN_CURSO");
        intentar("Mateo intenta tomarlo también", () -> servicio.tomar(1L, "mateo.diaz@soporteya.co"));
        servicio.resolver(1L, "carlos.ruiz@soporteya.co", "Se cambió la fuente de poder del equipo");
        System.out.println("Carlos lo resuelve → RESUELTO");
        servicio.cerrar(1L, "ana.perez@soporteya.co");
        System.out.println("Ana confirma → CERRADO");

        titulo("6. Indicadores del mes por técnico");
        servicio.indicadores(YearMonth.now()).forEach(i -> System.out.printf("%s: %d resueltos, %d%% a tiempo%n",
                i.tecnico(), i.resueltos(), i.cumplimiento()));

        titulo("7. Mis tickets de Ana (página 1)");
        var pagina = servicio.misTickets("ana.perez@soporteya.co", 0);
        pagina.forEach(t -> System.out.println(t.codigo() + " · " + t.estado() + " · " + t.asunto()));
        System.out.println("Total: " + pagina.getTotalElements());
    }

    private void intentar(String caso, Runnable accion) {
        try {
            accion.run();
            System.out.println("✗ " + caso + ": ¡no falló!");
        } catch (NegocioException e) {
            System.out.println("✓ " + caso + " → " + e.getMessage());
        }
    }

    private void titulo(String texto) {
        System.out.println();
        System.out.println("===== " + texto + " =====");
    }
}