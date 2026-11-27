package com.gym.controller;
import com.gym.model.*;
import com.gym.repository.*;
import com.gym.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UserRepo users;
  private final MemberRepo members;
  private final PaymentRepo payments;
  private final PasswordEncoder encoder;
  private final JwtUtil jwt;
  public AuthController(UserRepo u, MemberRepo m, PaymentRepo p, PasswordEncoder e, JwtUtil j){
    this.users=u; this.members=m; this.payments=p; this.encoder=e; this.jwt=j;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Map<String,String> b){
    try{
      if(users.findByPhone(b.get("phone")).isPresent())
        return ResponseEntity.badRequest().body(Map.of("error","Namba imeshatumika"));
      User u=new User();
      u.setFullName(b.getOrDefault("fullName", b.getOrDefault("name","Member")));
      u.setPhone(b.get("phone"));
      u.setPasswordHash(encoder.encode(b.get("password")));
      u.setRole("MEMBER");
      u.setActive(true);
      u.setStatus("ACTIVE");
      u.setSubscriptionStatus("ACTIVE");
      try{ if(b.get("gymId")!=null && !b.get("gymId").isBlank()) u.setGymId(Long.valueOf(b.get("gymId"))); }catch(Exception ignored){}
      u=users.save(u);
      try{
        Member m=new Member();
        m.setUserId(u.getId());
        m.setGymId(u.getGymId());
        members.save(m);
      }catch(Exception e){ System.out.println("Member save failed: "+e.getMessage()); }
      return ResponseEntity.ok(Map.of("message","Umejisajili! Ingia sasa"));
    }catch(Exception e){ e.printStackTrace(); return ResponseEntity.status(500).body(Map.of("error","REGISTER FAILED","reason", String.valueOf(e.getMessage()))); }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String,String> b){
    try{
      var u = users.findByPhone(b.get("phone")).orElse(null);
      if(u==null || !u.isActive() || !encoder.matches(b.getOrDefault("password",""), u.getPasswordHash()))
        return ResponseEntity.status(401).body(Map.of("error","Namba au password sio sahihi"));
      String token = jwt.generate(u.getId(), u.getGymId(), u.getRole(), u.getFullName());
      Map<String,Object> userMap = new HashMap<>();
      userMap.put("id", u.getId());
      userMap.put("name", u.getFullName());
      userMap.put("fullName", u.getFullName());
      userMap.put("phone", u.getPhone());
      userMap.put("role", u.getRole());
      userMap.put("gymId", u.getGymId());
      userMap.put("gym_id", u.getGymId());
      return ResponseEntity.ok(Map.of("token",token,"user",userMap));
    }catch(Exception e){ e.printStackTrace(); return ResponseEntity.status(500).body(Map.of("error","LOGIN FAILED","reason", String.valueOf(e.getMessage()))); }
  }

  @GetMapping("/seed-owner")
  public ResponseEntity<?> seed(){
    try{
      String phone="0770000000";
      if(users.findByPhone(phone).isPresent()) return ResponseEntity.ok(Map.of("message","Owner tayari yupo","phone",phone,"password","123456"));
      User u=new User();
      u.setFullName("Owner Zanzibar");
      u.setPhone(phone);
      u.setPasswordHash(encoder.encode("123456"));
      u.setRole("OWNER");
      u.setActive(true);
      u.setStatus("ACTIVE");
      u.setSubscriptionStatus("ACTIVE");
      users.save(u);
      return ResponseEntity.ok(Map.of("message","Owner ametengenezwa","phone",phone,"password","123456"));
    }catch(Exception e){ e.printStackTrace(); return ResponseEntity.status(500).body(Map.of("error","SEED FAILED","reason", String.valueOf(e.getMessage()))); }
  }

  @PostMapping("/pay")
  public ResponseEntity<?> pay(@RequestBody Map<String,String> b){
    try{
      Payment p=new Payment();
      p.setPhone(b.get("phone")); p.setPlan(b.get("plan")); p.setReference(b.get("reference"));
      String plan = b.getOrDefault("plan","");
      int amount = "KUBWA".equalsIgnoreCase(plan) ? 300000 : "KIDOGO".equalsIgnoreCase(plan) ? 0 : 30000;
      p.setAmount(amount);
      p.setStatus("PENDING");
      payments.save(p);
      return ResponseEntity.ok(Map.of("message","Malipo yamepokelewa - tunathibitisha"));
    }catch(Exception e){ e.printStackTrace(); return ResponseEntity.status(500).body(Map.of("error","PAY FAILED","reason", String.valueOf(e.getMessage()))); }
  }
}
