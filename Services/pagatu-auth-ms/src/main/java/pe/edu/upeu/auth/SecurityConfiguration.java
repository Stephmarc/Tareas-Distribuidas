package pe.edu.upeu.auth;

import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

  @Bean
  PasswordEncoder passwords() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  RSAKey signingKey() throws Exception {
    KeyPairGenerator g = KeyPairGenerator.getInstance("RSA");
    g.initialize(2048);
    KeyPair p = g.generateKeyPair();
    return new RSAKey.Builder((RSAPublicKey) p.getPublic())
      .privateKey((RSAPrivateKey) p.getPrivate())
      .keyID(java.util.UUID.randomUUID().toString())
      .build();
  }

  @Bean
  JwtEncoder encoder(RSAKey k) {
    return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(k)));
  }

  @Bean
  SecurityFilterChain api(HttpSecurity h) throws Exception {
    return h
      .csrf(c -> c.disable())
      .authorizeHttpRequests(a ->
        a
          .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR)
          .permitAll()
          .requestMatchers(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/oauth2/jwks",
            "/.well-known/openid-configuration",
            "/actuator/health/**"
          )
          .permitAll()
          .anyRequest()
          .denyAll()
      )
      .build();
  }
}
