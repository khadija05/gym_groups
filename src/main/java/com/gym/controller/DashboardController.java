package com.gym.controller;
import com.gym.model.*; import com.gym.repository.*;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*; import java.time.LocalDate; import java.util.*;

@RestController @RequestMapping("/api") @CrossOrigin(origins="*")
public class DashboardController {
  private final UserRepo users; private final GymRepo gyms; private final MemberRepo members; private final PaymentRepo payments; private final AttendanceRepo attendance; private final WorkoutRepo workouts;
  public DashboardController(UserRepo u,GymRepo g,MemberRepo m,PaymentRepo p,AttendanceRepo a,WorkoutRepo w){ this.users=u; this.gyms=g; this.members=m; this.payments=p; this.attendance=a; this.workouts=w; }

  @GetMapping("/dashboard") public ResponseEntity<?> dash(){
    Map<String,Object> d=new HashMap<>();
    d.put("gyms", gyms.count()); d.put("members", members.count()); d.put("income", payments.sumAll()); d.put("today", attendance.countByDate(LocalDate.now()));
    var list=new ArrayList<Map<String,Object>>();
    for(var m: members.findAll()){ Map<String,Object> x=new HashMap<>(); x.put("id",m.getId()); x.put("full_name",m.getFullName()); x.put("phone",m.getPhone()); x.put("plan",m.getPlan()); x.put("status",m.getDisplayStatus()); x.put("end_date",String.valueOf(m.getEndDate())); x.put("gymId",m.getGymId()); list.add(x); }
    d.put("list", list); d.put("workout","Pushups 3x10, Squats 3x12, Plank 1 min");
    return ResponseEntity.ok(d);
  }

  @PostMapping("/pay") public ResponseEntity<?> pay(@RequestBody Map<String,Object> b){
    try{ Long mid=Long.valueOf(String.valueOf(b.get("member_id"))); Integer amt=Integer.valueOf(String.valueOf(b.get("amount")));
      var mOpt=members.findById(mid); if(mOpt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Member hajapatikana"));
      var m=mOpt.get(); Payment p=new Payment(); p.setMemberId(mid); p.setGymId(m.getGymId()); p.setAmount(amt); p.setPlan("Mwezi 1"); p.setPhone(m.getPhone()); p.setGymName("Gym"); p.setStatus("PENDING"); payments.save(p);
      LocalDate now=LocalDate.now(); if(m.getEndDate()!=null && m.getEndDate().isAfter(now)) m.setEndDate(m.getEndDate().plusDays(30)); else m.setEndDate(now.plusDays(30)); m.setStatus("ACTIVE"); members.save(m);
      return ResponseEntity.ok(Map.of("message","✅ Malipo yamepokelewa!")); }catch(Exception e){ return ResponseEntity.status(500).body(Map.of("error",String.valueOf(e.getMessage()))); }
  }

  @PostMapping("/workout") public ResponseEntity<?> workout(@RequestBody Map<String,Object> b){
    Workout w=new Workout(); w.setMemberId(Long.valueOf(String.valueOf(b.get("member_id")))); w.setPlanText(String.valueOf(b.get("plan_text"))); workouts.save(w);
    return ResponseEntity.ok(Map.of("message","✅ Workout imetumwa!"));
  }

  @PostMapping("/progress") public ResponseEntity<?> progress(@RequestBody Map<String,Object> b){
    var all=members.findAll(); if(!all.isEmpty()){ var m=all.get(all.size()-1); try{ m.setWeightStart(Double.valueOf(String.valueOf(b.get("weightStart")))); m.setWeightNow(Double.valueOf(String.valueOf(b.get("weightNow")))); m.setWeightGoal(Double.valueOf(String.valueOf(b.get("weightGoal")))); members.save(m); }catch(Exception ignored){} }
    return ResponseEntity.ok(Map.of("message","✅ Imehifadhiwa!"));
  }

  @PostMapping("/coach") public ResponseEntity<?> coach(@RequestBody Map<String,String> b){
    User u=new User(); u.setFullName(b.getOrDefault("full_name","Kocha")); u.setPhone(b.getOrDefault("phone","")); u.setPasswordHash(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(b.getOrDefault("password","123456"))); u.setRole("COACH"); u.setStatus("ACTIVE"); users.save(u);
    return ResponseEntity.ok(Map.of("message","✅ Kocha ameongezwa!"));
  }
}
