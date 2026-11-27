package com.gym.controller;
import com.gym.model.*; import com.gym.repository.*;
import jakarta.servlet.http.HttpServletRequest; import org.springframework.http.ResponseEntity; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import java.time.LocalDate; import java.util.*;

@RestController @RequestMapping("/api")
public class DashboardController {
  private final UserRepo users; private final GymRepo gyms; private final MemberRepo members; private final PaymentRepo payments; private final AttendanceRepo attendance; private final WorkoutRepo workouts; private final PasswordEncoder encoder;
  public DashboardController(UserRepo u,GymRepo g,MemberRepo m,PaymentRepo p,AttendanceRepo a,WorkoutRepo w,PasswordEncoder e){this.users=u;this.gyms=g;this.members=m;this.payments=p;this.attendance=a;this.workouts=w;this.encoder=e;}

  private String role(HttpServletRequest r){ Object v=r.getAttribute("role"); return v==null?"":v.toString().toLowerCase(); }
  private Long gymId(HttpServletRequest r){ Object v=r.getAttribute("gymId"); if(v==null) v=r.getAttribute("gym_id"); if(v==null) return null; try{return Long.valueOf(v.toString());}catch(Exception e){return null;} }
  private Long userId(HttpServletRequest r){ Object v=r.getAttribute("userId"); if(v==null) v=r.getAttribute("user_id"); if(v==null) return null; try{return Long.valueOf(v.toString());}catch(Exception e){return null;} }
  private long safeLong(Long v){ return v==null?0:v; }

  @GetMapping("/dashboard") public ResponseEntity<?> dashboard(HttpServletRequest r){
    String ro = role(r);
    if(ro.isEmpty()) return ResponseEntity.ok(Map.of("role","guest","gyms",0,"members",0,"income",0,"today",0,"list",List.of()));
    if("owner".equals(ro)){
      Map<String,Object> res=new HashMap<>();
      long gc=0, mc=0, inc=0;
      try{ gc=gyms.count(); }catch(Exception e){}
      try{ mc=users.findAll().stream().filter(u->u.getRole()!=null && u.getRole().equalsIgnoreCase("member")).count(); }catch(Exception e){}
      try{ inc=safeLong(payments.sumAll()); }catch(Exception e){}
      res.put("role",ro); res.put("gyms",gc); res.put("members",mc); res.put("income",inc); res.put("today",mc); res.put("list",List.of());
      return ResponseEntity.ok(res);
    }
    if("admin".equals(ro)||"coach".equals(ro)||"trainer".equals(ro)){
      Long g=gymId(r);
      // Kama gymId ni null, mtafute Admin kwenye DB
      if(g==null && userId(r)!=null){ var a=users.findById(userId(r)).orElse(null); if(a!=null) g=a.getGymId(); }
      final Long gg=g;
      List<User> all = users.findAll();
      long mc = all.stream().filter(u->u.getRole()!=null && u.getRole().equalsIgnoreCase("member")).filter(u->gg==null || u.getGymId()==null || u.getGymId().equals(gg)).count();
      long today=0, inc=0;
      try{ today=attendance.countByGymIdAndDate(gg,LocalDate.now()); }catch(Exception e){}
      try{ inc=safeLong(payments.sumByGym(gg)); }catch(Exception e){}
      List<Map<String,Object>> list=new ArrayList<>();
      for(User u : all){
        if(u.getRole()==null || !u.getRole().equalsIgnoreCase("member")) continue;
        if(gg!=null && u.getGymId()!=null && !u.getGymId().equals(gg)) continue;
        var m=members.findByUserId(u.getId()).orElse(null);
        Map<String,Object> mm=new HashMap<>();
        mm.put("id",u.getId()); mm.put("full_name",u.getFullName()==null?"":u.getFullName()); mm.put("phone",u.getPhone()==null?"":u.getPhone());
        mm.put("plan",m==null||m.getPlan()==null?"Mwezi 1":m.getPlan()); mm.put("end_date",m==null||m.getEndDate()==null?"":m.getEndDate().toString());
        mm.put("status",(m!=null && m.getEndDate()!=null && m.getEndDate().isBefore(LocalDate.now()))?"Imeisha":"Active");
        list.add(mm);
      }
      Map<String,Object> res=new HashMap<>(); res.put("role",ro); res.put("members",mc); res.put("today",today); res.put("income",inc); res.put("list",list);
      return ResponseEntity.ok(res);
    }
    var m=members.findByUserId(userId(r)).orElse(null);
    var w=workouts.findTopByMemberIdOrderByIdDesc(userId(r)).orElse(null);
    Map<String,Object> mem=new HashMap<>(); mem.put("plan",m==null||m.getPlan()==null?"Mwezi 1":m.getPlan()); mem.put("end_date",m==null||m.getEndDate()==null?"":m.getEndDate().toString()); mem.put("weight_now",m==null||m.getWeightNow()==null?0:m.getWeightNow()); mem.put("weight_start",m==null||m.getWeightStart()==null?0:m.getWeightStart()); mem.put("weight_goal",m==null||m.getWeightGoal()==null?0:m.getWeightGoal());
    Map<String,Object> res=new HashMap<>(); res.put("role",ro); res.put("member",mem); res.put("workout",w==null?"Hakuna mpango bado, muulize Kocha":w.getPlanText()); res.put("members",1); res.put("income",0); res.put("today",0);
    return ResponseEntity.ok(res);
  }

  // ===== OWNER AKISAJILI ADMIN + GYM =====
  @PostMapping("/owner/create-gym") public ResponseEntity<?> createGym(@RequestBody Map<String,String> b, HttpServletRequest r){
    if(!"owner".equals(role(r))) return ResponseEntity.status(403).body(Map.of("error","Huna ruhusa - Ingia kama Owner"));
    if(b.get("adminPhone")==null || b.get("adminPhone").isBlank()) return ResponseEntity.badRequest().body(Map.of("error","Simu ya Admin inahitajika"));
    if(users.findByPhone(b.get("adminPhone")).isPresent()) return ResponseEntity.badRequest().body(Map.of("error","Admin huyo tayari yupo - tumia namba nyingine"));
    Gym g=new Gym(); g.setName(b.get("gymName")); g.setLocation(b.get("location")); g=gyms.save(g);
    User a=new User(); a.setGymId(g.getId()); a.setFullName(b.get("adminName")); a.setPhone(b.get("adminPhone")); a.setPasswordHash(encoder.encode(b.get("adminPass")==null?"123456":b.get("adminPass"))); a.setRole("admin"); a.setActive(true); users.save(a);
    return ResponseEntity.ok(Map.of("message","Gym na Admin vimetengenezwa"));
  }

  @GetMapping("/owner/gyms") public ResponseEntity<?> allGyms(HttpServletRequest r){
    if(!"owner".equals(role(r))) return ResponseEntity.status(403).body(Map.of("error","Huna ruhusa"));
    var list=gyms.findAll().stream().map(g->{ Map<String,Object> mm=new HashMap<>(); mm.put("id",g.getId()); mm.put("name",g.getName()==null?"":g.getName()); mm.put("location",g.getLocation()==null?"":g.getLocation()); long c=users.findAll().stream().filter(u->u.getRole()!=null && u.getRole().equalsIgnoreCase("member") && g.getId().equals(u.getGymId())).count(); mm.put("members",c); return mm; }).toList();
    return ResponseEntity.ok(list);
  }

  @PostMapping("/members") public ResponseEntity<?> addMember(@RequestBody Map<String,String> b, HttpServletRequest r){
    if(!List.of("admin","coach","trainer").contains(role(r))) return ResponseEntity.status(403).body(Map.of("error","Huna ruhusa - Ingia kama Admin"));
    if(users.findByPhone(b.get("phone")).isPresent()) return ResponseEntity.badRequest().body(Map.of("error","Namba imeshatumika"));
    Long gid=gymId(r); if(gid==null && userId(r)!=null){ var a=users.findById(userId(r)).orElse(null); if(a!=null) gid=a.getGymId(); }
    User u=new User(); u.setGymId(gid); u.setFullName(b.get("full_name")); u.setPhone(b.get("phone")); u.setPasswordHash(encoder.encode(b.get("password")==null?"123456":b.get("password"))); u.setRole("member"); u.setActive(true); u=users.save(u);
    Member m=new Member(); m.setUserId(u.getId()); m.setGymId(gid); m.setPlan(b.getOrDefault("plan","Mwezi 1")); int days="Wiki 1".equals(m.getPlan())?7:"Miezi 3".equals(m.getPlan())?90:30; m.setEndDate(LocalDate.now().plusDays(days)); members.save(m);
    return ResponseEntity.ok(Map.of("message","Member amesajiliwa"));
  }

  @PostMapping("/coach") public ResponseEntity<?> addCoach(@RequestBody Map<String,String> b, HttpServletRequest r){
    if(!"admin".equals(role(r))) return ResponseEntity.status(403).body(Map.of("error","Huna ruhusa"));
    User u=new User(); u.setGymId(gymId(r)); u.setFullName(b.get("full_name")); u.setPhone(b.get("phone")); u.setPasswordHash(encoder.encode(b.get("password")==null?"123456":b.get("password"))); u.setRole("coach"); u.setActive(true); users.save(u);
    return ResponseEntity.ok(Map.of("message","Kocha ameongezwa"));
  }
  @PostMapping("/pay") public ResponseEntity<?> pay(@RequestBody Map<String,Object> b, HttpServletRequest r){
    Long mid=Long.valueOf(b.get("member_id").toString()); Payment p=new Payment(); p.setGymId(gymId(r)); p.setMemberId(mid); p.setAmount(Integer.valueOf(b.get("amount").toString())); p.setStatus("PAID"); payments.save(p);
    members.findByUserIdAndGymId(mid,gymId(r)).ifPresent(m->{ m.setEndDate(m.getEndDate()==null?LocalDate.now().plusDays(30):(m.getEndDate().isBefore(LocalDate.now())?LocalDate.now().plusDays(30):m.getEndDate().plusDays(30))); members.save(m); });
    return ResponseEntity.ok(Map.of("message","Malipo yamepokelewa"));
  }
  @PostMapping("/workout") public ResponseEntity<?> workout(@RequestBody Map<String,Object> b, HttpServletRequest r){
    WorkoutPlan w=new WorkoutPlan(); w.setGymId(gymId(r)); w.setMemberId(Long.valueOf(b.get("member_id").toString())); w.setCoachId(userId(r)); w.setPlanText((String)b.get("plan_text")); workouts.save(w);
    return ResponseEntity.ok(Map.of("message","Workout imetumwa"));
  }
  @PostMapping("/progress") public ResponseEntity<?> progress(@RequestBody Map<String,Object> b, HttpServletRequest r){
    var m=members.findByUserId(userId(r)).orElse(null); if(m==null) return ResponseEntity.badRequest().body(Map.of("error","Member hayupo"));
    if(b.get("weightStart")!=null) m.setWeightStart(Double.valueOf(b.get("weightStart").toString()));
    if(b.get("weightNow")!=null) m.setWeightNow(Double.valueOf(b.get("weightNow").toString()));
    if(b.get("weightGoal")!=null) m.setWeightGoal(Double.valueOf(b.get("weightGoal").toString()));
    members.save(m); return ResponseEntity.ok(Map.of("message","Maendeleo yamehifadhiwa"));
  }
}
