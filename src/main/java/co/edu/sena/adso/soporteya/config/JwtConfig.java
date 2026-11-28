package co.edu.sena.adso.soporteya.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

// Una sola clave secreta firma (encoder) y verifica (decoder) los tokens: algoritmo HS256
@Configuration
public class JwtConfig {

    private final SecretKey clave;

    public JwtConfig(@Value("${soporteya.jwt.secreto}") String secreto) {
        if (secreto.length() < 32) {
            throw new IllegalStateException("JWT_SECRETO debe tener al menos 32 caracteres");
        }
        this.clave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(clave));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(clave).build();
    }
}