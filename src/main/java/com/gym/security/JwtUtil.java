package com.gym.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

  private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private final long EXP = 1000L * 60 * 60 * 24 * 7; // siku 7

  public String generate(Long userId, Long gymId, String role, String name){
    return Jwts.builder()
      .setSubject(String.valueOf(userId))
      .claim("gymId", gymId)
      .claim("role", role)
      .claim("name", name)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + EXP))
      .signWith(key)
      .compact();
  }

  // HII NDIO ILIKUWA INAKOSWA — SASA IPO
  public boolean validate(String token){
    try{
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    }catch(Exception e){
      return false;
    }
  }

  // Alias — ili kama faili nyingine inaita validateToken, isigome
  public boolean validateToken(String token){ return validate(token); }
  public boolean isValid(String token){ return validate(token); }

  public Claims getClaims(String token){
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  public Long getUserId(String token){
    try{ return Long.valueOf(getClaims(token).getSubject()); }catch(Exception e){ return null; }
  }

  public String getRole(String token){
    try{ return (String) getClaims(token).get("role"); }catch(Exception e){ return null; }
  }

  public Long getGymId(String token){
    try{
      Object v = getClaims(token).get("gymId");
      if(v==null) return null;
      return Long.valueOf(String.valueOf(v));
    }catch(Exception e){ return null; }
  }
}
