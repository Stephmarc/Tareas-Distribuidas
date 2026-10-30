package pe.edu.upeu.orden.config;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.*;

@Configuration
@EnableFeignClients(basePackages = "pe.edu.upeu.orden.client")
public class FeignConfiguration {

  @Bean
  RequestInterceptor traceInterceptor() {
    return template -> {
      String id = MDC.get("traceId");
      if (id != null) template.header("X-Trace-ID", id);
      var auth =
        org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
      if (
        auth instanceof
          org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken token
      ) template.header(
        "Authorization",
        "Bearer " + token.getToken().getTokenValue()
      );
    };
  }
}
