package co.edu.sena.adso.soporteya.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import co.edu.sena.adso.soporteya.model.Area;
import co.edu.sena.adso.soporteya.model.Categoria;
import co.edu.sena.adso.soporteya.model.EstadoTicket;
import co.edu.sena.adso.soporteya.model.Prioridad;
import co.edu.sena.adso.soporteya.model.Rol;
import co.edu.sena.adso.soporteya.model.Ticket;
import co.edu.sena.adso.soporteya.model.Usuario;
import co.edu.sena.adso.soporteya.repository.AreaRepository;
import co.edu.sena.adso.soporteya.repository.CategoriaRepository;
import co.edu.sena.adso.soporteya.repository.TicketRepository;
import co.edu.sena.adso.soporteya.repository.UsuarioRepository;

// Datos de prueba realistas. Solo se cargan si la base de datos está vacía.
@Component
@Order(1)
public class DatosIniciales implements CommandLineRunner {

    private final AreaRepository areas;
    private final CategoriaRepository categorias;
    private final UsuarioRepository usuarios;
    private final TicketRepository tickets;
    private final PasswordEncoder encoder;

    public DatosIniciales(AreaRepository areas, CategoriaRepository categorias,
                          UsuarioRepository usuarios, TicketRepository tickets, PasswordEncoder encoder) {
        this.areas = areas;
        this.categorias = categorias;
        this.usuarios = usuarios;
        this.tickets = tickets;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarios.count() > 0) {
            return;
        }
        Area contabilidad = new Area("Contabilidad");
        Area talento = new Area("Talento Humano");
        Area comercial = new Area("Comercial");
        Area tecnologia = new Area("Tecnología");
        areas.saveAll(List.of(contabilidad, talento, comercial, tecnologia));

        Categoria hardware = new Categoria("Hardware", "Computadores, pantallas, teclados y otros equipos");
        Categoria software = new Categoria("Software", "Programas, instalaciones y actualizaciones");
        Categoria red = new Categoria("Red e internet", "Conexión, wifi, cableado y VPN");
        Categoria impresoras = new Categoria("Impresoras", "Impresión, escáner y tóner");
        Categoria accesos = new Categoria("Accesos y contraseñas", "Usuarios bloqueados, permisos y correo");
        categorias.saveAll(List.of(hardware, software, red, impresoras, accesos));

        Usuario laura = new Usuario("Laura Gómez", "laura.gomez@soporteya.co", Rol.COORDINADOR, tecnologia);
        Usuario carlos = new Usuario("Carlos Ruiz", "carlos.ruiz@soporteya.co", Rol.TECNICO, tecnologia);
        carlos.getEspecialidades().addAll(List.of(hardware, impresoras));
        Usuario diana = new Usuario("Diana Torres", "diana.torres@soporteya.co", Rol.TECNICO, tecnologia);
        diana.getEspecialidades().addAll(List.of(software, accesos));
        Usuario mateo = new Usuario("Mateo Díaz", "mateo.diaz@soporteya.co", Rol.TECNICO, tecnologia);
        mateo.getEspecialidades().addAll(List.of(red, hardware));
        Usuario ana = new Usuario("Ana Pérez", "ana.perez@soporteya.co", Rol.SOLICITANTE, contabilidad);
        Usuario jorge = new Usuario("Jorge Herrera", "jorge.herrera@soporteya.co", Rol.SOLICITANTE, comercial);
        Usuario paola = new Usuario("Paola Martínez", "paola.martinez@soporteya.co", Rol.SOLICITANTE, talento);
        // Unidad 3 · contraseña de prueba para todos, guardada como hash BCrypt
        String hash = encoder.encode("Soporte2026");
        List<Usuario> todos = List.of(laura, carlos, diana, mateo, ana, jorge, paola);
        todos.forEach(u -> u.setPassword(hash));
        usuarios.saveAll(todos);

        Ticket t1 = new Ticket("El computador no enciende",
                "Desde esta mañana el equipo de escritorio no prende. La luz del botón no se enciende.",
                Prioridad.ALTA, ana, hardware);
        Ticket t2 = new Ticket("No puedo entrar al sistema contable",
                "Dice que mi usuario está bloqueado. Tengo que cerrar el mes hoy.",
                Prioridad.CRITICA, ana, accesos);
        t2.setTecnico(diana);
        t2.cambiarEstado(EstadoTicket.EN_CURSO, diana, "Tomado por Diana Torres");
        Ticket t3 = new Ticket("La impresora del segundo piso imprime manchado",
                "Las hojas salen con rayas negras a un lado.", Prioridad.BAJA, paola, impresoras);
        Ticket t4 = new Ticket("Internet lento en la oficina comercial",
                "Las videollamadas con clientes se cortan desde ayer.", Prioridad.MEDIA, jorge, red);
        t4.setTecnico(mateo);
        t4.cambiarEstado(EstadoTicket.EN_CURSO, mateo, "Tomado por Mateo Díaz");
        t4.cambiarEstado(EstadoTicket.RESUELTO, mateo, "Solución: se reinició el router y se cambió el canal del wifi");
        Ticket t5 = new Ticket("Instalar el antivirus en el portátil nuevo",
                "Me entregaron un portátil y no tiene antivirus.", Prioridad.MEDIA, jorge, software);
        t5.setTecnico(diana);
        t5.cambiarEstado(EstadoTicket.EN_CURSO, diana, "Tomado por Diana Torres");
        t5.cambiarEstado(EstadoTicket.RESUELTO, diana, "Solución: antivirus instalado y actualizado");
        t5.cambiarEstado(EstadoTicket.CERRADO, jorge, "Confirmado por el solicitante");
        Ticket t6 = new Ticket("El correo no sincroniza en el celular",
                "En el computador llegan los correos, pero en el celular no.", Prioridad.BAJA, paola, software);
        tickets.saveAll(List.of(t1, t2, t3, t4, t5, t6));   // cascade: guarda también los comentarios
    }
}