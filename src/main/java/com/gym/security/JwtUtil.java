package com.gym.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

  private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private final long EXP = 1000L * 60 * 60 * 24 * 7; // siku 7

  public String generate(Long userId, String role, Long gymId) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);
    claims.put("gymId", gymId);
    claims.put("gym_id", gymId);
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(String.valueOf(userId))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXP))
        .signWith(key)
        .compact();
  }

  private Claims parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  public boolean validate(String token) {
    try { parse(token); return true; } catch (Exception e) { return false; }
  }

  public Long getUserId(String token) {
    return Long.valueOf(parse(token).getSubject());
  }

  public String getRole(String token) {
    Object r = parse(token).get("role");
    return r == null ? "" : r.toString();
  }

  public Long getGymId(String token) {
    Object g = parse(token).get("gymId");
    if (g == null) g = parse(token).get("gym_id");
    if (g == null) return null;
    try { return Long.valueOf(g.toString()); } catch (Exception e) { return null; }
  }
}
