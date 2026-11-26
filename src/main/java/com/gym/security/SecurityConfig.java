package com.gym.security;
import org.springframework.context.annotation.*; import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.web.SecurityFilterChain; import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; import org.springframework.web.cors.*; import java.util.List;
@Configuration
public class SecurityConfig {
  private final JwtFilter jwtFilter; public SecurityConfig(JwtFilter f){this.jwtFilter=f;}
  @Bean public PasswordEncoder encoder(){ return new BCryptPasswordEncoder(); }
  @Bean public SecurityFilterChain filter(HttpSecurity http) throws Exception {
    http.csrf(c->c.disable()).cors(c->c.configurationSource(req->{ var cfg=new CorsConfiguration(); cfg.setAllowedOrigins(List.of("https://gym-zanzibar.vercel.app","http://localhost:5173","http://localhost:5500")); cfg.setAllowedMethods(List.of("*")); cfg.setAllowedHeaders(List.of("*")); return cfg; }))
      .authorizeHttpRequests(a->a.requestMatchers("/api/auth/**","/api/gym/groups","/api/gym/attendance","/").permitAll().anyRequest().authenticated())
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
