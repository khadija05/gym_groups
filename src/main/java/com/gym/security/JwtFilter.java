package com.gym.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;
  public JwtFilter(JwtUtil jwtUtil){ this.jwtUtil = jwtUtil; }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String path = request.getServletPath();

    // RUHUSU BILA TOKEN — HII NDIO INAYOONDOA 403
    if(path.startsWith("/api/auth/") || path.startsWith("/api/health") || path.startsWith("/api/public/") || path.equals("/") || path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js") || path.equals("/actuator/health")){
      filterChain.doFilter(request, response);
      return;
    }

    String header = request.getHeader("Authorization");
    if(header != null && header.startsWith("Bearer ")){
      try{
        String token = header.substring(7);
        if(jwtUtil.validate(token)){
          // Token ni sahihi, endelea
          filterChain.doFilter(request, response);
          return;
        }
      }catch(Exception e){}
    }
    // Kama ni /api/** bila token, kataa — lakini usikatae /api/auth
    if(path.startsWith("/api/")){
      response.setStatus(401);
      response.setContentType("application/json");
      response.getWriter().write("{\"error\":\"Token inahitajika\"}");
      return;
    }
    filterChain.doFilter(request, response);
  }
}
