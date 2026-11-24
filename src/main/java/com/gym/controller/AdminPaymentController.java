package com.gym.controller;
import com.gym.model.*;
import com.gym.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*; import java.time.LocalDateTime;
@RestController @RequestMapping("/api/admin-payment")
public class AdminPaymentController {
@Autowired AdminPaymentRepository payRepo; @Autowired UserRepository userRepo;
@PostMapping("/pay")
public Map<String,Object> pay(@RequestBody Map<String,Object> b){
Map<String,Object> r=new HashMap<>();
Long adminId=null; try{ adminId=Long.parseLong(b.get("adminId").toString()); }catch(Exception e){ r.put("success",false); r.put("message","Admin ID inahitajika"); return r; }
Optional<User> au=userRepo.findById(adminId); if(au.isEmpty()){ r.put("success",false); r.put("message","Admin hajapatikana"); return r; }
AdminPayment p=new AdminPayment(); p.setAdminId(adminId); p.setAdminName(au.get().getName());
try{ p.setAmount(Double.parseDouble(b.getOrDefault("amount","50000").toString())); }catch(Exception e){ p.setAmount(50000.0); }
p.setMonth((String)b.getOrDefault("month","")); p.setReference((String)b.getOrDefault("reference","")); p.setMethod((String)b.getOrDefault("method","M-Pesa")); p.setStatus("pending"); p.setCreatedAt(LocalDateTime.now());
payRepo.save(p); r.put("success",true); r.put("message","Malipo yametumwa, subiri Owner athibitishe"); return r;
}
@GetMapping("/my/{adminId}") public List<AdminPayment> my(@PathVariable Long adminId){ return payRepo.findByAdminId(adminId); }
@GetMapping("/pending") public List<AdminPayment> pending(){ return payRepo.findByStatus("pending"); }
@PostMapping("/approve/{id}")
public Map<String,Object> approve(@PathVariable Long id){
Map<String,Object> r=new HashMap<>(); Optional<AdminPayment> o=payRepo.findById(id); if(o.isEmpty()){ r.put("success",false); r.put("message","Haikupatikana"); return r; }
AdminPayment p=o.get(); p.setStatus("paid"); payRepo.save(p);
Optional<User> au=userRepo.findById(p.getAdminId()); if(au.isPresent()){ User u=au.get(); u.setStatus("active"); u.setSubscriptionStatus("active"); u.setSubscriptionExpiresAt(LocalDateTime.now().plusMonths(1)); userRepo.save(u); }
r.put("success",true); r.put("message","Imethibitishwa, Admin amefunguliwa"); return r;
}
@PostMapping("/reject/{id}")
public Map<String,Object> reject(@PathVariable Long id){
Map<String,Object> r=new HashMap<>(); Optional<AdminPayment> o=payRepo.findById(id); if(o.isEmpty()){ r.put("success",false); return r; }
AdminPayment p=o.get(); p.setStatus("rejected"); payRepo.save(p); r.put("success",true); r.put("message","Imekataliwa"); return r;
}
}
