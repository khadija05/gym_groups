package com.gym.controller;
import com.gym.model.*;
import com.gym.repository.*;
import com.gym.security.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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

  @PersistenceContext
  private EntityManager em;

  public AuthController(UserRepo u, MemberRepo m, PaymentRepo p, PasswordEncoder e, JwtUtil j){
    this.users=u;
    this.members=m;
    this.payments=p;
    this.encoder=e;
    this.jwt=j;
  }

  // 1. REKEBISHA DATABASE — FUNGUA HII KWANZA
  @GetMapping("/fix-db")
  @Transactional
  public ResponseEntity<?> fixDb(){
    try{
      String[] sqls = {
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS full_name VARCHAR(255)",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS name VARCHAR(255)",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS username VARCHAR(255)",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS gym_id BIGINT",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT true",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS status VARCHAR(255) DEFAULT 'ACTIVE'",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS subscription_status VARCHAR(255) DEFAULT 'ACTIVE'",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW()",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS expires_at TIMESTAMP",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS subscription_expires_at TIMESTAMP",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS subscription_expires_date DATE",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS trial_ends_at DATE",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255)",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(255)",
        "ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(255)"
      };
      for(String s : sqls){ em.createNativeQuery(s).executeUpdate(); }
      try{ em.createNativeQuery("UPDATE users SET full_name = COALESCE(full_name, name, phone) WHERE full_name IS NULL").executeUpdate(); }catch(Exception ignored){}
      try{ em.createNativeQuery("UPDATE users SET name = COALESCE(name, full_name, phone) WHERE name IS NULL").executeUpdate(); }catch(Exception ignored){}
      return ResponseEntity.ok(Map.of("message","Database imerekebishwa! Sasa jaribu seed-owner"));
    }catch(Exception e){
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("error","FIX FAILED","reason", String.valueOf(e.getMessage())));
    }
  }

  // 2. REGISTER
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Map<String,String> b){
    try{
      String phone = b.get("phone");
      String password = b.get("password");
      if(phone==null || phone.isBlank() || password==null || password.isBlank()){
        return ResponseEntity.badRequest().body(Map.of("error","Simu na password zinahitajika"));
      }
      if(users.findByPhone(phone).isPresent()){
        return ResponseEntity.badRequest().body(Map.of("error","Namba imeshatumika"));
      }
      User u = new User();
      u.setFullName(b.getOrDefault("fullName", b.getOrDefault("name","Member")));
      u.setPhone(phone);
      u.setPasswordHash(encoder.encode(password));
      u.setRole("MEMBER");
      u.setActive(true);
      u.setStatus("ACTIVE");
      u.setSubscriptionStatus("ACTIVE");
      try{
        if(b.get("gymId")!=null && !b.get("gymId").isBlank()){
          u.setGymId(Long.valueOf(b.get("gymId")));
        }
      }catch(Exception ignored){}
      u = users.save(u);
      try{
        Member m = new Member();
        m.setUserId(u.getId());
        m.setGymId(u.getGymId());
        m.setPlan("Mwezi 1");
        members.save(m);
      }catch(Exception e){
        System.out.println("Member save failed (not fatal): " + e.getMessage());
      }
      return ResponseEntity.ok(Map.of("message","Umejisajili! Ingia sasa"));
    }catch(Exception e){
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("error","REGISTER FAILED","reason", String.valueOf(e.getMessage())));
    }
  }

  // 3. LOGIN
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String,String> b){
    try{
      String phone = b.get("phone");
      String password = b.getOrDefault("password","");
      var u = users.findByPhone(phone).orElse(null);
      if(u==null || !u.isActive() || u.getPasswordHash()==null || !encoder.matches(password, u.getPasswordHash())){
        return ResponseEntity.status(401).body(Map.of("error","Namba au password sio sahihi"));
      }
      String token = jwt.generate(u.getId(), u.getGymId(), u.getRole(), u.getFullName());
      Map<String,Object> userMap = new HashMap<>();
      userMap.put("id", u.getId());
      userMap.put("name", u.getFullName());
      userMap.put("fullName", u.getFullName());
      userMap.put("phone", u.getPhone());
      userMap.put("role", u.getRole());
      userMap.put("gymId", u.getGymId());
      userMap.put("gym_id", u.getGymId());
      userMap.put("isActive", u.isActive());
      userMap.put("status", u.getStatus());
      return ResponseEntity.ok(Map.of("token",token,"user",userMap));
    }catch(Exception e){
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("error","LOGIN FAILED","reason", String.valueOf(e.getMessage())));
    }
  }

  // 4. SEED OWNER
  @GetMapping("/seed-owner")
  public ResponseEntity<?> seed(){
    try{
      String phone = "0770000000";
      if(users.findByPhone(phone).isPresent()){
        return ResponseEntity.ok(Map.of("message","Owner tayari yupo","phone",phone,"password","123456"));
      }
      User u = new User();
      u.setFullName("Owner Zanzibar");
      u.setPhone(phone);
      u.setPasswordHash(encoder.encode("123456"));
      u.setRole("OWNER");
      u.setActive(true);
      u.setStatus("ACTIVE");
      u.setSubscriptionStatus("ACTIVE");
      users.save(u);
      return ResponseEntity.ok(Map.of("message","Owner ametengenezwa","phone",phone,"password","123456"));
    }catch(Exception e){
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("error","SEED FAILED","reason", String.valueOf(e.getMessage())));
    }
  }

  // 5. PAY
  @PostMapping("/pay")
  public ResponseEntity<?> pay(@RequestBody Map<String,String> b){
    try{
      Payment p = new Payment();
      p.setPhone(b.get("phone"));
      p.setPlan(b.get("plan"));
      p.setReference(b.get("reference"));
      String plan = b.getOrDefault("plan","");
      int amount = "KUBWA".equalsIgnoreCase(plan) ? 300000 : "KIDOGO".equalsIgnoreCase(plan) ? 0 : 30000;
      p.setAmount(amount);
      p.setStatus("PENDING");
      payments.save(p);
      return ResponseEntity.ok(Map.of("message","Malipo yamepokelewa - tunathibitisha"));
    }catch(Exception e){
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("error","PAY FAILED","reason", String.valueOf(e.getMessage())));
    }
  }
}
