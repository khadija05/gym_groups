package com.gym.security;
import io.jsonwebtoken.Claims; import io.jsonwebtoken.Jwts; import io.jsonwebtoken.SignatureAlgorithm; import io.jsonwebtoken.security.Keys; import org.springframework.stereotype.Component; import java.security.Key; import java.util.*;
@Component
public class JwtUtil {
  private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private final long EXP = 1000L*60*60*24*7;
  public String generate(Long userId, String role, Long gymId){
    Map<String,Object> c=new HashMap<>(); c.put("role",role); c.put("gymId",gymId); c.put("gym_id",gymId);
    return Jwts.builder().setClaims(c).setSubject(String.valueOf(userId)).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis()+EXP)).signWith(key).compact();
  }
  private Claims parse(String t){ return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(t).getBody(); }
  public boolean validate(String t){ try{parse(t);return true;}catch(Exception e){return false;} }
  public Long getUserId(String t){ return Long.valueOf(parse(t).getSubject()); }
  public String getRole(String t){ Object r=parse(t).get("role"); return r==null?"":r.toString(); }
  public Long getGymId(String t){ Object g=parse(t).get("gymId"); if(g==null) g=parse(t).get("gym_id"); if(g==null) return null; try{return Long.valueOf(g.toString());}catch(Exception e){return null;} }
}
