package com.gym.controller;
import com.gym.model.*; import com.gym.repository.*;
import jakarta.servlet.http.HttpServletRequest; import org.springframework.http.ResponseEntity; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import java.time.LocalDate; import java.util.*;
@RestController @RequestMapping("/api")
public class DashboardController {
  private final UserRepo users; private final GymRepo gyms; private final MemberRepo members; private final PaymentRepo payments; private final AttendanceRepo attendance; private final WorkoutRepo workouts; private final PasswordEncoder encoder;
  public DashboardController(UserRepo u,GymRepo g,MemberRepo m,PaymentRepo p,AttendanceRepo a,WorkoutRepo w,PasswordEncoder e){this.users=u;this.gyms=g;this.members=m;this.payments=p;this.attendance=a;this.workouts=w;this.encoder=e;}
  private String role(HttpServletRequest r){ return (String) r.getAttribute("role"); }
  private Long gymId(HttpServletRequest r){ return (Long) r.getAttribute("gymId"); }
  private Long userId(HttpServletRequest r){ return (Long) r.getAttribute("userId"); }

  @GetMapping("/dashboard") public ResponseEntity<?> dashboard(HttpServletRequest r){
    String ro = role(r);
    if("owner".equals(ro)) return ResponseEntity.ok(Map.of("role",ro,"gyms",gyms.count(),"members",users.countByRole("member"),"income",payments.sumAll()));
    if("admin".equals(ro)||"coach".equals(ro)){
      Long g=gymId(r);
      var list = users.findByGymIdAndRole(g,"member").stream().map(u->{ var m=members.findByUserId(u.getId()).orElse(null); return Map.of("id",u.getId(),"full_name",u.getFullName(),"phone",u.getPhone(),"plan",m==null?"":m.getPlan(),"end_date",m==null?"":m.getEndDate().toString(),"status",m!=null && m.getEndDate().isBefore(LocalDate.now())?"Imeisha":"Inafanya"); }).toList();
      return ResponseEntity.ok(Map.of("role",ro,"members",users.countByGymIdAndRole(g,"member"),"today",attendance.countByGymIdAndDate(g,LocalDate.now()),"income",payments.sumByGym(g),"list",list));
    }
    var m = members.findByUserId(userId(r)).orElse(null);
    var w = workouts.findTopByMemberIdOrderByIdDesc(userId(r)).orElse(null);
    return ResponseEntity.ok(Map.of("role",ro,"member",m==null?Map.of():Map.of("plan",m.getPlan(),"end_date",m.getEndDate().toString(),"weight_now",m.getWeightNow()==null?0:m.getWeightNow(),"weight_start",m.getWeightStart()==null?0:m.getWeightStart()),"workout",w==null?"Hakuna mpango bado, muulize Kocha":w.getPlanText()));
  }

  @PostMapping("/owner/create-gym") public ResponseEntity<?> createGym(@RequestBody Map<String,String> b, HttpServletRequest r){
    if(!"owner".equals(role(r))) return ResponseEntity.status(403).body(Map.of("error","Huna ruhusa"));
    Gym g=new Gym(); g.setName(b.get("gymName")); g.setLocation(b.get("location")); g=gyms.save(g);
    User a=new User(); a.setGymId(g.getId()); a.setFullName(b.get("adminName")); a.setPhone(b.get("adminPhone")); a.setPasswordHash(encoder.encode(b.get("adminPass"))); a.setRole("admin"); users.save(a);
    return ResponseEntity.ok(Map.of("message","Gym na Admin vimetengenezwa"));
  }
  @GetMapping("/owner/gyms") public ResponseEntity<?> allGyms(HttpServletRequest r){
    if(!"owner".equals(role(r))) return ResponseEntity.status(403).body(Map.of("error","Huna ruhusa"));
    var list = gyms.findAll().stream().map(g->Map.of("id",g.getId(),"name",g.getName(),"location",g.getLocation()==null?"":g.getLocation(),"members",users.countByGymIdAndRole(g.getId(),"member"))).toList();
    return ResponseEntity.ok(list);
  }
  @PostMapping("/members") public ResponseEntity<?> addMember(@RequestBody Map<String,String> b, HttpServletRequest r){
    if(!List.of("admin","coach").contains(role(r))) return ResponseEntity.status(403).body(Map.of("error","Huna ruhusa"));
    if(users.findByPhone(b.get("phone")).isPresent()) return ResponseEntity.badRequest().body(Map.of("error","Namba imeshatumika"));
    User u=new User(); u.setGymId(gymId(r)); u.setFullName(b.get("full_name")); u.setPhone(b.get("phone")); u.setPasswordHash(encoder.encode(b.get("password"))); u.setRole("member"); u=users.save(u);
    Member m=new Member(); m.setUserId(u.getId()); m.setGymId(gymId(r)); m.setPlan(b.getOrDefault("plan","Mwezi 1")); int days="Wiki 1".equals(m.getPlan())?7:"Miezi 3".equals(m.getPlan())?90:30; m.setEndDate(LocalDate.now().plusDays(days)); members.save(m);
    return ResponseEntity.ok(Map.of("message","Member amesajiliwa"));
  }
  @PostMapping("/coach") public ResponseEntity<?> addCoach(@RequestBody Map<String,String> b, HttpServletRequest r){
    if(!"admin".equals(role(r))) return ResponseEntity.status(403).body(Map.of("error","Huna ruhusa"));
    User u=new User(); u.setGymId(gymId(r)); u.setFullName(b.get("full_name")); u.setPhone(b.get("phone")); u.setPasswordHash(encoder.encode(b.get("password"))); u.setRole("coach"); users.save(u);
    return ResponseEntity.ok(Map.of("message","Kocha ameongezwa"));
  }
  @PostMapping("/pay") public ResponseEntity<?> pay(@RequestBody Map<String,Object> b, HttpServletRequest r){
    Long mid=Long.valueOf(b.get("member_id").toString()); Payment p=new Payment(); p.setGymId(gymId(r)); p.setMemberId(mid); p.setAmount(Integer.valueOf(b.get("amount").toString())); p.setStatus("PAID"); payments.save(p);
    members.findByUserIdAndGymId(mid,gymId(r)).ifPresent(m->{ m.setEndDate(m.getEndDate().isBefore(LocalDate.now())?LocalDate.now().plusDays(30):m.getEndDate().plusDays(30)); members.save(m); });
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
