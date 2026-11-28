package co.edu.sena.adso.soporteya.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SeguridadConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Unidad 4 · el mismo «verificador» de usuario y contraseña, para el formulario y para pedir un token
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService usuarios, PasswordEncoder encoder) {
        DaoAuthenticationProvider proveedor = new DaoAuthenticationProvider(usuarios);
        proveedor.setPasswordEncoder(encoder);
        return new ProviderManager(proveedor);
    }

    // Puerta 1 · /api/v1/** para otras aplicaciones: sin sesión, sin CSRF, con token JWT
    @Bean
    @Order(1)
    public SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/v1/**")
            .authorizeHttpRequests(acceso -> acceso
                .requestMatchers(HttpMethod.POST, "/api/v1/token").permitAll()
                .anyRequest().authenticated())
            .csrf(csrf -> csrf.disable())                 // no hay cookies de sesión que robar
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(Customizer.withDefaults())              // usa el CorsConfigurationSource de abajo
            .oauth2ResourceServer(recurso -> recurso
                .jwt(jwt -> jwt.jwtAuthenticationConverter(rolesDelToken())));
        return http.build();
    }

    // Puerta 2 · las páginas web: sesión, formulario de login y CSRF (igual que en la Unidad 3)
    @Bean
    @Order(2)
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(acceso -> acceso
                .requestMatchers("/", "/login", "/registro", "/css/**", "/js/**", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("COORDINADOR")
                .requestMatchers("/tablero/**", "/api/web/tablero/**").hasAnyRole("TECNICO", "COORDINADOR")
                .anyRequest().authenticated())
            .formLogin(login -> login
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/", false)
                .permitAll())
            .logout(salida -> salida
                .logoutSuccessUrl("/login?salio")
                .permitAll())
            .exceptionHandling(errores -> errores
                .accessDeniedPage("/acceso-denegado"));
        return http.build();
    }

    // El token trae "roles": ["COORDINADOR"]. Así los @PreAuthorize("hasRole(…)") de los servicios
    // funcionan igual con sesión que con token.
    private JwtAuthenticationConverter rolesDelToken() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(roles);
        return convertidor;
    }

    // CORS: qué páginas de OTROS sitios pueden llamar a la API desde el navegador
    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${soporteya.cors.origenes}") List<String> origenes) {
        CorsConfiguration reglas = new CorsConfiguration();
        reglas.setAllowedOrigins(origenes);
        reglas.setAllowedMethods(List.of("GET", "POST"));
        reglas.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/api/v1/**", reglas);
        return fuente;
    }
}