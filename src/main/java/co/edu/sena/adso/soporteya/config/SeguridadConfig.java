package co.edu.sena.adso.soporteya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity   // activa @PreAuthorize en los servicios
public class SeguridadConfig {

    // Las contraseñas se guardan como hash BCrypt: no se pueden «descifrar»
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(acceso -> acceso
                // Públicas: portada, login, registro y archivos estáticos
                .requestMatchers("/", "/login", "/registro", "/css/**", "/js/**", "/error").permitAll()
                // Por rol: la regla más específica va primero
                .requestMatchers("/admin/**").hasRole("COORDINADOR")
                .requestMatchers("/tablero/**").hasAnyRole("TECNICO", "COORDINADOR")
                // Todo lo demás: basta con haber iniciado sesión
                .anyRequest().authenticated())
            .formLogin(login -> login
                .loginPage("/login")              // nuestra página, no la de Spring
                .usernameParameter("email")       // el campo del formulario se llama email
                .defaultSuccessUrl("/", false)
                .permitAll())
            .logout(salida -> salida
                .logoutSuccessUrl("/login?salio")
                .permitAll())
            .exceptionHandling(errores -> errores
                .accessDeniedPage("/acceso-denegado"));
        return http.build();
    }
}