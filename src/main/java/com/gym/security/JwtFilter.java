package com.gym.security;
import jakarta.servlet.*; import jakarta.servlet.http.*; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter; import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil; public JwtFilter(JwtUtil j){this.jwtUtil=j;}
  @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
    String h = req.getHeader("Authorization");
    if(h!=null && h.startsWith("Bearer ")){
      try{ var c = jwtUtil.parse(h.substring(7)); req.setAttribute("userId", Long.parseLong(c.getSubject())); req.setAttribute("gymId", c.get("gymId", Long.class)); req.setAttribute("role", c.get("role", String.class)); req.setAttribute("name", c.get("name", String.class)); }
      catch(Exception e){ res.setStatus(401); res.getWriter().write("{\"error\":\"Token imeisha\"}"); return; }
    }
    chain.doFilter(req,res);
  }
}
