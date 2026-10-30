package pe.edu.upeu.auth;

import com.nimbusds.jose.jwk.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {

  public record Credenciales(
    @NotBlank String username,
    @NotBlank @Size(min = 8, max = 72) String password
  ) {}

  private final UsuarioRepository usuarios;
  private final PasswordEncoder passwords;
  private final JwtEncoder encoder;
  private final RSAKey key;

  public AuthController(
    UsuarioRepository usuarios,
    PasswordEncoder passwords,
    JwtEncoder encoder,
    RSAKey key
  ) {
    this.usuarios = usuarios;
    this.passwords = passwords;
    this.encoder = encoder;
    this.key = key;
  }

  @PostMapping("/api/v1/auth/register")
  @ResponseStatus(HttpStatus.CREATED)
  @Transactional
  public Map<String, Object> register(@Valid @RequestBody Credenciales c) {
    if (
      usuarios.findByUsername(c.username()).isPresent()
    ) throw new ResponseStatusException(HttpStatus.CONFLICT);
    Usuario u = new Usuario();
    u.username = c.username();
    u.password = passwords.encode(c.password());
    u.roles.add("CLIENTE");
    usuarios.save(u);
    return Map.of("id", u.id, "username", u.username);
  }

  @PostMapping("/api/v1/auth/login")
  public Map<String, Object> login(@Valid @RequestBody Credenciales c) {
    Usuario u = usuarios
      .findByUsername(c.username())
      .filter(v -> passwords.matches(c.password(), v.password))
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    Instant now = Instant.now();
    var claims = JwtClaimsSet.builder()
      .issuer("http://localhost:8085")
      .subject(u.id.toString())
      .issuedAt(now)
      .expiresAt(now.plusSeconds(900))
      .claim("idCliente", u.id.toString())
      .claim("realm_access", Map.of("roles", u.roles))
      .build();
    String token = encoder
      .encode(JwtEncoderParameters.from(claims))
      .getTokenValue();
    return Map.of(
      "access_token",
      token,
      "token_type",
      "Bearer",
      "expires_in",
      900
    );
  }

  @GetMapping("/oauth2/jwks")
  public Map<String, Object> jwks() {
    return new JWKSet(key.toPublicJWK()).toJSONObject();
  }

  @GetMapping("/.well-known/openid-configuration")
  public Map<String, Object> discovery() {
    return Map.of(
      "issuer",
      "http://localhost:8085",
      "jwks_uri",
      "http://localhost:8085/oauth2/jwks"
    );
  }
}
