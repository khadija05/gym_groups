
package com.gym.controller;

import com.gym.model.Member;
import com.gym.model.User;
import com.gym.repository.MemberRepo;
import com.gym.repository.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

  private final MemberRepo members;
  private final UserRepo users;
  private final PasswordEncoder encoder;

  public MemberController(MemberRepo m, UserRepo u, PasswordEncoder e){
    this.members = m; this.users = u; this.encoder = e;
  }

  // MEMBER WOTE - KWA OWNER: GYM ZOTE, KWA ADMIN: GYM YAKE
  @GetMapping
  public ResponseEntity<?> getAll(){
    var list = new ArrayList<Map<String,Object>>();
    for(var m : members.findAll()){
      Map<String,Object> map = new HashMap<>();
      map.put("id", m.getId());
      map.put("fullName", m.getFullName());
      map.put("full_name", m.getFullName());
      map.put("phone", m.getPhone());
      map.put("gymId", m.getGymId());
      map.put("gym_id", m.getGymId());
      map.put("plan", m.getPlan());
      map.put("status", m.getDisplayStatus());
      map.put("role", "MEMBER");
      map.put("startDate", String.valueOf(m.getStartDate()));
      map.put("endDate", String.valueOf(m.getEndDate()));
      map.put("end_date", String.valueOf(m.getEndDate()));
      map.put("daysLeft", m.getDaysLeft());
      map.put("warning", m.getWarning());
      map.put("weightStart", m.getWeightStart());
      map.put("weightNow", m.getWeightNow());
      map.put("weightGoal", m.getWeightGoal());
      // Jina kutoka User kama Member hana jina
      if(m.getFullName() == null || m.getFullName().isEmpty()){
        try{
          if(m.getUserId() != null){
            var u = users.findById(m.getUserId());
            if(u.isPresent()){
              map.put("fullName", u.get().getFullName());
              map.put("full_name", u.get().getFullName());
              map.put("phone", u.get().getPhone());
            }
          }
        }catch(Exception ignored){}
      }
      list.add(map);
    }
    Collections.reverse(list);
    return ResponseEntity.ok(list);
  }

  // MEMBER MMOJA - VIEW
  @GetMapping("/{id}")
  public ResponseEntity<?> getOne(@PathVariable Long id){
    var opt = members.findById(id);
    if(opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error","Member hajapatikana"));
    var m = opt.get();
    Map<String,Object> map = new HashMap<>();
    map.put("id", m.getId());
    map.put("fullName", m.getFullName());
    map.put("phone", m.getPhone());
    map.put("gymId", m.getGymId());
    map.put("plan", m.getPlan());
    map.put("status", m.getDisplayStatus());
    map.put("startDate", String.valueOf(m.getStartDate()));
    map.put("endDate", String.valueOf(m.getEndDate()));
    map.put("daysLeft", m.getDaysLeft());
    map.put("warning", m.getWarning());
    map.put("weightStart", m.getWeightStart());
    map.put("weightNow", m.getWeightNow());
    map.put("weightGoal", m.getWeightGoal());
    return ResponseEntity.ok(map);
  }

  // SAJILI MEMBER MPYA - KWA ADMIN
  @PostMapping
  public ResponseEntity<?> create(@RequestBody Map<String,String> b){
    try{
      String name = b.getOrDefault("full_name", b.getOrDefault("fullName",""));
      String phone = b.getOrDefault("phone","");
      String pass = b.getOrDefault("password","123456");
      String plan = b.getOrDefault("plan","Mwezi 1");
      if(name.isEmpty() || phone.isEmpty()){
        return ResponseEntity.badRequest().body(Map.of("error","Jaza Jina na Simu"));
      }
      // 1. Tengeneza User
      User u = new User();
      u.setFullName(name);
      u.setPhone(phone);
      u.setPassword(encoder.encode(pass));
      u.setRole("MEMBER");
      try{ u.setStatus("ACTIVE"); }catch(Exception ignored){}
      // gymId kutoka kwa Admin aliyeingia - kama haina, weka null
      try{
        if(b.get("gymId") != null) u.setGymId(Long.valueOf(b.get("gymId")));
      }catch(Exception ignored){}
      users.save(u);

      // 2. Tengeneza Member
      Member m = new Member();
      m.setUserId(u.getId());
      m.setGymId(u.getGymId());
      m.setFullName(name);
      m.setPhone(phone);
      m.setPassword(encoder.encode(pass));
      m.setPlan(plan);
      m.setStatus("ACTIVE");
      m.setStartDate(LocalDate.now());
      // Wiki 1 = siku 7, Miezi 3 = siku 90, Mwezi 1 = siku 30
      int days = plan.contains("Wiki") ? 7 : plan.contains("3") ? 90 : 30;
      m.setEndDate(LocalDate.now().plusDays(days));
      members.save(m);

      return ResponseEntity.ok(Map.of("message","✅ Member amesajiliwa! Siku zilobaki: " + days, "id", m.getId()));
    }catch(Exception e){
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("error", String.valueOf(e.getMessage())));
    }
  }

  // KUMBUKUSHA MEMBER - inarudisha namba na ujumbe tayari kwa WhatsApp
  @GetMapping("/{id}/remind")
  public ResponseEntity<?> remind(@PathVariable Long id){
    var opt = members.findById(id);
    if(opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error","Hayajapatikana"));
    var m = opt.get();
    String msg;
    if(m.getDaysLeft() <= 0){
      msg = "Habari " + m.getFullName() + "! Package yako ya GYM ZANZIBAR IMEISHA. Tafadhali lipia sasa ili uendelee na mazoezi. Asante!";
    } else if(m.getDaysLeft() <= 5){
      msg = "Habari " + m.getFullName() + "! Kumbusho: Package yako ya GYM ZANZIBAR inabaki siku " + m.getDaysLeft() + " tu. Inaisha " + m.getEndDate() + ". Lipia kabla haijaisha. Asante!";
    } else {
      msg = "Habari " + m.getFullName() + "! Package yako inabaki siku " + m.getDaysLeft() + ". Inaisha " + m.getEndDate() + ". Asante!";
    }
    return ResponseEntity.ok(Map.of("phone", m.getPhone(), "message", msg, "daysLeft", m.getDaysLeft()));
  }
}
