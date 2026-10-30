package pe.edu.upeu.cliente;

import java.util.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

  @Bean
  SecurityFilterChain api(HttpSecurity http) throws Exception {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
      Map<String, Object> access = jwt.getClaim("realm_access");
      List<?> roles =
        access == null
          ? List.of()
          : (List<?>) access.getOrDefault("roles", List.of());
      return roles
        .stream()
        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
        .map(a -> (org.springframework.security.core.GrantedAuthority) a)
        .toList();
    });
    return http
      .csrf(c -> c.disable())
      .authorizeHttpRequests(a ->
        a
          .requestMatchers("/actuator/health/**", "/actuator/prometheus")
          .permitAll()
          .requestMatchers("/api/v1/clientes/**")
          .hasRole("ADMIN")
          .anyRequest()
          .hasRole("ADMIN")
      )
      .oauth2ResourceServer(o ->
        o.jwt(j -> j.jwtAuthenticationConverter(converter))
      )
      .build();
  }
}
