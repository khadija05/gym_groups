package com.gym.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  public JwtFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String path = request.getRequestURI();

    // Ruhusu login, register, seed, na static files bila token
    if (path.contains("/api/auth/") || path.contains("/login.html") || path.contains("/index.html")
        || path.contains(".css") || path.contains(".js") || path.equals("/")) {
      chain.doFilter(request, response);
      return;
    }

    String auth = request.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      try {
        String token = auth.substring(7);
        if (jwtUtil.validate(token)) {
          Long userId = jwtUtil.getUserId(token);
          String role = jwtUtil.getRole(token); // OWNER, ADMIN, MEMBER, COACH
          Long gymId = jwtUtil.getGymId(token);

          // WEKA KWENYE REQUEST — DashboardController inasoma hizi
          request.setAttribute("userId", userId);
          request.setAttribute("user_id", userId);
          // Tuma role kwa herufi ndogo ili Controller isilinganishe vibaya
          request.setAttribute("role", role == null ? "" : role.toLowerCase());
          request.setAttribute("gymId", gymId);
          request.setAttribute("gym_id", gymId);

          // Weka pia kwenye Spring Security
          var authToken = new UsernamePasswordAuthenticationToken(
              userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + (role == null ? "" : role.toUpperCase()))));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      } catch (Exception e) {
        // Token mbovu - acha iende, Controller itarudisha 401
        System.out.println("JWT ERROR: " + e.getMessage());
      }
    }
    chain.doFilter(request, response);
  }
}
