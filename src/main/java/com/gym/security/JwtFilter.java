package com.gym.security;
import jakarta.servlet.*; import jakarta.servlet.http.*; import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; import org.springframework.security.core.authority.SimpleGrantedAuthority; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter; import java.io.IOException; import java.util.List;
@Component
public class JwtFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil; public JwtFilter(JwtUtil j){this.jwtUtil=j;}
  @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
    String path=req.getRequestURI();
    if(path.contains("/api/auth/")||path.contains(".html")||path.contains(".css")||path.contains(".js")||path.equals("/")){ chain.doFilter(req,res); return; }
    String auth=req.getHeader("Authorization");
    if(auth!=null && auth.startsWith("Bearer ")){
      try{ String token=auth.substring(7); if(jwtUtil.validate(token)){ Long uid=jwtUtil.getUserId(token); String role=jwtUtil.getRole(token); Long gid=jwtUtil.getGymId(token);
        req.setAttribute("userId",uid); req.setAttribute("user_id",uid); req.setAttribute("role",role==null?"":role.toLowerCase()); req.setAttribute("gymId",gid); req.setAttribute("gym_id",gid);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(uid,null,List.of(new SimpleGrantedAuthority("ROLE_"+(role==null?"":role.toUpperCase())))));
      }}catch(Exception e){ System.out.println("JWT ERR: "+e.getMessage()); }
    }
    chain.doFilter(req,res);
  }
}
