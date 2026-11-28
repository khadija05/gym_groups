package com.gym.controller;
import com.gym.model.*; import com.gym.repository.*;
import jakarta.annotation.PostConstruct; import org.springframework.http.ResponseEntity; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import java.time.LocalDate; import java.time.temporal.ChronoUnit; import java.util.*;

@RestController
@RequestMapping({"/api/gym", "/api/owner"})
public class GymController {
  private final UserRepo users; private final GymRepo gyms; private final MemberRepo members; private final PaymentRepo payments; private final AttendanceRepo attendance; private final WorkoutRepo workouts; private final PasswordEncoder encoder;
  public GymController(UserRepo u,GymRepo g,MemberRepo m,PaymentRepo p,AttendanceRepo a,WorkoutRepo w,PasswordEncoder e){this.users=u;this.gyms=g;this.members=m;this.payments=p;this.attendance=a;this.workouts=w;this.encoder=e;}

  // Vikundi 4 vya default - vinaundwa mara ya kwanza tu
  @PostConstruct public void seed(){
    if(gyms.count()>0) return;
    String[][] d = {{"Gym Stone Town - Jenga Mwili","Kocha Juma","Jumatatu - Jumamosi","5:00 Asubuhi - 9:00 Jioni","30000","25","🏋️","Kujenga misuli","#FFD400"},{"Gym Mjini - Punguza Uzito","Kocha Amina","Jumatatu, Jumatano, Ijumaa","6:00 Asubuhi • 6:00 Jioni","30000","18","🔥","Punguza uzito","#FF6B00"},{"Gym Forodhani - Wanawake Tu","Kocha Fatma","Jumanne, Alhamisi, Jumamosi","6:00 Jioni - 8:00 Jioni","25000","12","🧘","Wanawake tu","#FF69B4"},{"Gym Mjini Magharibi - Wanaume Tu","Kocha Ali","Kila Siku","5:00 Asubuhi - 10:00 Jioni","30000","22","💪","Wanaume tu","#00D4FF"}};
    for(String[] x: d){
      Gym g=new Gym(); g.setName(x[0]); g.setCoach(x[1]); g.setDays(x[2]); g.setTimeSlot(x[3]); g.setPrice(Integer.valueOf(x[4])); g.setMembersCount(Integer.valueOf(x[5])); g.setEmoji(x[6]); g.setDescription(x[7]); g.setColor(x[8]);
      // Kwa Gym za default - Weka kama ACTIVE na siku 30
      try{ g.setStatus("ACTIVE"); }catch(Exception ignored){}
      try{ g.setPackageEndDate(LocalDate.now().plusDays(30)); }catch(Exception ignored){}
      try{ g.setLocation("Zanzibar"); }catch(Exception ignored){}
      gyms.save(g);
    }
  }

  @GetMapping("/groups") public List<Gym> groups(){ return gyms.findAll(); }

  @PostMapping("/attendance") public ResponseEntity<?> attend(@RequestBody Map<String,String> b){
    Attendance a=new Attendance(); a.setMemberName(b.get("memberName")); a.setGroupName(b.get("groupName")); attendance.save(a);
    return ResponseEntity.ok(Map.of("message","Amehudhuria"));
  }
  @GetMapping("/attendance/today") public Map<String,Object> today(){ return Map.of("count", attendance.countByDate(LocalDate.now()), "list", attendance.findByDateOrderByIdDesc(LocalDate.now())); }

  // ============ MPYA KWA OWNER ============

  // 1. GYM ZOTE KWA OWNER - NA SIKU ZILIZOBAKI + ADMIN + WARNING
  // Inafanya kazi kwa /api/owner/gyms NA /api/gym/gyms
  @GetMapping("/gyms")
  public ResponseEntity<?> getGymsForOwner(){
    var list = new ArrayList<Map<String,Object>>();
    for(var g : gyms.findAll()){
      Map<String,Object> m = new HashMap<>();
      m.put("id", g.getId());
      m.put("name", g.getName());
      try{ m.put("location", g.getLocation()); }catch(Exception e){ m.put("location","Zanzibar"); }
      try{ m.put("status", g.getStatus()); }catch(Exception e){ m.put("status","ACTIVE"); }
      try{ m.put("adminName", g.getAdminName()); }catch(Exception e){ m.put("adminName", g.getCoach()); }
      try{ m.put("adminPhone", g.getAdminPhone()); }catch(Exception e){ m.put("adminPhone",""); }
      try{ m.put("coach", g.getCoach()); }catch(Exception ignored){}
      try{ m.put("members", g.getMembersCount()); }catch(Exception e){ m.put("members", 0); }
      try{ m.put("packageEndDate", String.valueOf(g.getPackageEndDate())); }catch(Exception e){ m.put("packageEndDate",""); }

      long daysLeft = 30;
      try{
        if(g.getPackageEndDate() != null){
          daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), g.getPackageEndDate());
        }
      }catch(Exception ignored){}
      m.put("daysLeft", daysLeft);
      String warning = daysLeft <= 0 ? "IMESHAISHA ❌" : daysLeft <= 5 ? "INAKARIBIA KUISHIA ⚠️" : "SAWA ✅";
      m.put("warning", warning);
      list.add(m);
    }
    return ResponseEntity.ok(list);
  }

  // 2. TENGEZA GYM MPYA + ADMIN - KWA OWNER
  // Inafanya kazi kwa /api/owner/create-gym NA /api/gym/create-gym
  @PostMapping("/create-gym")
  public ResponseEntity<?> createGym(@RequestBody Map<String,String> b){
    try{
      String gymName = b.getOrDefault("gymName", b.getOrDefault("name","Gym Mpya"));
      String location = b.getOrDefault("location","Zanzibar");
      String adminName = b.getOrDefault("adminName","Admin");
      String adminPhone = b.getOrDefault("adminPhone", b.getOrDefault("phone",""));
      String adminPass = b.getOrDefault("adminPass", b.getOrDefault("password","123456"));

      if(gymName == null || gymName.trim().isEmpty()){
        return ResponseEntity.badRequest().body(Map.of("error","Jaza Jina la Gym"));
      }
      if(adminPhone == null || adminPhone.trim().length() < 10){
        return ResponseEntity.badRequest().body(Map.of("error","Simu ya Admin si sahihi"));
      }

      // 1. Tengeneza Gym
      Gym g = new Gym();
      g.setName(gymName);
      try{ g.setLocation(location); }catch(Exception ignored){}
      try{ g.setCoach(adminName); }catch(Exception ignored){}
      try{ g.setAdminName(adminName); }catch(Exception ignored){}
      try{ g.setAdminPhone(adminPhone); }catch(Exception ignored){}
      try{ g.setStatus("PENDING"); }catch(Exception ignored){}
      try{ g.setPackageEndDate(LocalDate.now()); }catch(Exception ignored){}
      try{ g.setMembersCount(0); }catch(Exception ignored){}
      try{ g.setPrice(30000); }catch(Exception ignored){}
      try{ g.setEmoji("🏋️"); }catch(Exception ignored){}
      try{ g.setDescription("Gym Mpya"); }catch(Exception ignored){}
      try{ g.setColor("#FFD400"); }catch(Exception ignored){}
      try{ g.setDays("Kila Siku"); }catch(Exception ignored){}
      try{ g.setTimeSlot("5:00 Asubuhi - 9:00 Jioni"); }catch(Exception ignored){}
      gyms.save(g);

      // 2. Tengeneza Admin kama User
      try{
        User admin = new User();
        admin.setFullName(adminName);
        admin.setPhone(adminPhone);
        admin.setPassword(encoder.encode(adminPass));
        admin.setRole("ADMIN");
        admin.setGymId(g.getId());
        try{ admin.setStatus("ACTIVE"); }catch(Exception ignored){}
        users.save(admin);
      }catch(Exception e){
        // Kama User haikuundwa, Gym tayari imetengenezwa - usirudishe error
        e.printStackTrace();
      }

      return ResponseEntity.ok(Map.of("message","✅ Gym imetengenezwa! Admin: " + adminName, "gymId", g.getId()));
    }catch(Exception e){
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("error", String.valueOf(e.getMessage())));
    }
  }

  // 3. GYM MOJA - VIEW
  @GetMapping("/gyms/{id}")
  public ResponseEntity<?> getOneGym(@PathVariable Long id){
    var opt = gyms.findById(id);
    if(opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error","Gym haijapatikana"));
    var g = opt.get();
    Map<String,Object> m = new HashMap<>();
    m.put("id", g.getId());
    m.put("name", g.getName());
    try{ m.put("location", g.getLocation()); }catch(Exception e){ m.put("location","Zanzibar"); }
    try{ m.put("status", g.getStatus()); }catch(Exception e){ m.put("status","ACTIVE"); }
    try{ m.put("adminName", g.getAdminName()); }catch(Exception e){ m.put("adminName", g.getCoach()); }
    try{ m.put("adminPhone", g.getAdminPhone()); }catch(Exception e){ m.put("adminPhone",""); }
    try{
      if(g.getPackageEndDate() != null){
        m.put("packageEndDate", String.valueOf(g.getPackageEndDate()));
        m.put("daysLeft", ChronoUnit.DAYS.between(LocalDate.now(), g.getPackageEndDate()));
      }
    }catch(Exception ignored){}
    return ResponseEntity.ok(m);
  }
}
