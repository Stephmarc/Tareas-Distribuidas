package pe.edu.upeu.catalogo;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

  @Bean
  SecurityFilterChain api(HttpSecurity h) throws Exception {
    return h
      .csrf(c -> c.disable())
      .authorizeHttpRequests(a ->
        a
          .requestMatchers("/actuator/health/**", "/actuator/prometheus")
          .permitAll()
          .anyRequest()
          .authenticated()
      )
      .oauth2ResourceServer(o -> o.jwt(j -> {}))
      .build();
  }
}
