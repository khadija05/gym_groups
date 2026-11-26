package com.gym.security;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Component; import java.util.*; import java.security.Key;
@Component
public class JwtUtil {
  @Value("${jwt.secret}") private String secret;
  @Value("${jwt.expiration}") private long exp;
  private Key key(){ return Keys.hmacShaKeyFor(secret.getBytes()); }
  public String generate(Long id, Long gymId, String role, String name){
    return Jwts.builder().setSubject(String.valueOf(id)).claim("gymId", gymId).claim("role", role).claim("name", name)
      .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis()+exp)).signWith(key(), SignatureAlgorithm.HS256).compact();
  }
  public Claims parse(String token){ return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody(); }
}
