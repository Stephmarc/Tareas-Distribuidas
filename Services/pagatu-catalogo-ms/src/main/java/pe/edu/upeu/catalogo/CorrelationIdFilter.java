package pe.edu.upeu.catalogo;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

  protected void doFilterInternal(
    HttpServletRequest q,
    HttpServletResponse r,
    FilterChain c
  ) throws ServletException, IOException {
    String id = q.getHeader("X-Trace-ID");
    if (id == null || !id.matches("[A-Za-z0-9._-]{1,100}")) id =
      UUID.randomUUID().toString();
    MDC.put("traceId", id);
    r.setHeader("X-Trace-ID", id);
    try {
      LoggerFactory.getLogger(getClass()).info(
        "{} {}",
        q.getMethod(),
        q.getRequestURI()
      );
      c.doFilter(q, r);
    } finally {
      MDC.remove("traceId");
    }
  }
}
