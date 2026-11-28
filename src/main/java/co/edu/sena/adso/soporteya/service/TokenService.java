package co.edu.sena.adso.soporteya.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import co.edu.sena.adso.soporteya.dto.TokenDTO;

// Emite un JWT para un usuario ya autenticado
@Service
public class TokenService {

    private static final Duration DURACION = Duration.ofHours(1);

    private final JwtEncoder encoder;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public TokenDTO emitir(Authentication autenticacion) {
        Instant ahora = Instant.now();
        List<String> roles = autenticacion.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring("ROLE_".length()))
                .toList();
        // El contenido (claims) viaja legible en el token: nunca ponga contraseñas ni datos sensibles
        JwtClaimsSet contenido = JwtClaimsSet.builder()
                .issuer("soporteya")
                .issuedAt(ahora)
                .expiresAt(ahora.plus(DURACION))
                .subject(autenticacion.getName())
                .claim("roles", roles)
                .build();
        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(cabecera, contenido)).getTokenValue();
        return new TokenDTO(token, "Bearer", DURACION.toSeconds());
    }
}