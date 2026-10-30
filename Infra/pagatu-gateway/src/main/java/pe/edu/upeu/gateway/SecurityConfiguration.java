package pe.edu.upeu.gateway;

import java.util.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfiguration {

  @Bean
  SecurityWebFilterChain api(ServerHttpSecurity h) {
    JwtAuthenticationConverter c = new JwtAuthenticationConverter();
    c.setJwtGrantedAuthoritiesConverter(jwt -> {
      Map<String, Object> a = jwt.getClaim("realm_access");
      List<?> roles =
        a == null ? List.of() : (List<?>) a.getOrDefault("roles", List.of());
      return roles
        .stream()
        .map(
          r ->
            (org.springframework.security.core.GrantedAuthority) new SimpleGrantedAuthority(
              "ROLE_" + r
            )
        )
        .toList();
    });
    return h
      .csrf(s -> s.disable())
      .cors(s -> {})
      .authorizeExchange(a ->
        a
          .pathMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
          .permitAll()
          .pathMatchers("/actuator/health/**", "/actuator/prometheus")
          .permitAll()
          .pathMatchers("/api/v1/clientes/**")
          .hasRole("ADMIN")
          .pathMatchers("/api/**")
          .hasAnyRole("ADMIN", "CLIENTE")
          .anyExchange()
          .denyAll()
      )
      .oauth2ResourceServer(o ->
        o.jwt(j ->
          j.jwtAuthenticationConverter(
            new ReactiveJwtAuthenticationConverterAdapter(c)
          )
        )
      )
      .build();
  }
}
