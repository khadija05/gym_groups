package com.gym.controller;
import com.gym.model.*; import com.gym.repository.*; import com.gym.security.JwtUtil;
import org.springframework.http.ResponseEntity; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
  private final UserRepo users; private final MemberRepo members; private final PaymentRepo payments; private final PasswordEncoder encoder; private final JwtUtil jwt;
  public AuthController(UserRepo u, MemberRepo m, PaymentRepo p, PasswordEncoder e, JwtUtil j){this.users=u;this.members=m;this.payments=p;this.encoder=e;this.jwt=j;}

  @PostMapping("/register") public ResponseEntity<?> register(@RequestBody Map<String,String> b){
    if(users.findByPhone(b.get("phone")).isPresent()) return ResponseEntity.badRequest().body(Map.of("error","Namba imeshatumika"));
    User u=new User(); u.setFullName(b.get("fullName")); u.setPhone(b.get("phone")); u.setPasswordHash(encoder.encode(b.get("password"))); u.setRole("member"); u.setGymId(b.get("gymId")!=null?Long.valueOf(b.get("gymId")):null); u=users.save(u);
    Member m=new Member(); m.setUserId(u.getId()); m.setGymId(u.getGymId()); members.save(m);
    return ResponseEntity.ok(Map.of("message","Umejisajili! Ingia sasa"));
  }

  @PostMapping("/login") public ResponseEntity<?> login(@RequestBody Map<String,String> b){
    var u = users.findByPhone(b.get("phone")).orElse(null);
    if(u==null || !u.isActive() || !encoder.matches(b.getOrDefault("password",""), u.getPasswordHash())) return ResponseEntity.status(401).body(Map.of("error","Namba au password sio sahihi"));
    String token = jwt.generate(u.getId(), u.getGymId(), u.getRole(), u.getFullName());
    return ResponseEntity.ok(Map.of("token",token,"user",Map.of("id",u.getId(),"name",u.getFullName(),"role",u.getRole(),"gym_id",u.getGymId()==null?"":u.getGymId())));
  }

  // Malipo ya PBZ - kutoka index.html
  @PostMapping("/pay") public ResponseEntity<?> pay(@RequestBody Map<String,String> b){
    Payment p=new Payment(); p.setPhone(b.get("phone")); p.setPlan(b.get("plan")); p.setReference(b.get("reference")); p.setAmount("KUBWA".equals(b.get("plan"))?300000:"KIDOGO".equals(b.get("plan"))?0:30000); p.setStatus("PENDING"); payments.save(p);
    return ResponseEntity.ok(Map.of("message","Malipo yamepokelewa - tunathibitisha"));
  }
}
