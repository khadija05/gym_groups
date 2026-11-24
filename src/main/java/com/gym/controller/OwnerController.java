package com.gym.controller;
import com.gym.model.*;
import com.gym.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/owner")
public class OwnerController {
@Autowired UserRepository userRepo; @Autowired PaymentRepository paymentRepo; @Autowired AdminPaymentRepository adminPayRepo; @Autowired MemberRepository memberRepo;
@GetMapping("/admins/{ownerId}") public List<User> admins(@PathVariable Long ownerId){ return userRepo.findByCreatedBy(ownerId); }
@GetMapping("/members/{ownerId}") public List<User> members(@PathVariable Long ownerId){ return userRepo.findByRoleAndCreatedBy("MEMBER", ownerId); }
@GetMapping("/pending/{ownerId}") public List<User> pending(@PathVariable Long ownerId){ List<User> all=userRepo.findByRoleAndCreatedBy("MEMBER", ownerId); List<User> out=new ArrayList<>(); for(User u: all) if("pending".equals(u.getStatus())) out.add(u); return out; }
@PostMapping("/approve/{id}") public Map<String,Object> approve(@PathVariable Long id){ Map<String,Object> r=new HashMap<>(); Optional<User> o=userRepo.findById(id); if(o.isEmpty()){r.put("success",false);return r;} User u=o.get(); u.setStatus("active"); u.setExpiresAt(java.time.LocalDateTime.now().plusMonths(1)); userRepo.save(u); r.put("success",true); r.put("message","Amekubaliwa"); return r; }
@PostMapping("/reject/{id}") public Map<String,Object> reject(@PathVariable Long id){ Map<String,Object> r=new HashMap<>(); Optional<User> o=userRepo.findById(id); if(o.isEmpty()){r.put("success",false);return r;} User u=o.get(); u.setStatus("locked"); userRepo.save(u); r.put("success",true); return r; }
@GetMapping("/payments") public List<Payment> payments(){ return paymentRepo.findAll(); }
@GetMapping("/admin-payments") public List<AdminPayment> adminPayments(){ return adminPayRepo.findAll(); }
@PostMapping("/admin-payment") public Map<String,Object> adminPayment(@RequestBody Map<String,Object> b){ Map<String,Object> r=new HashMap<>(); AdminPayment p=new AdminPayment(); p.setAdminName((String)b.get("adminName")); try{p.setAdminId(Long.parseLong(b.get("adminId").toString()));}catch(Exception e){} try{p.setAmount(Double.parseDouble(b.getOrDefault("amount","0").toString()));}catch(Exception e){} p.setMonth((String)b.getOrDefault("month","")); p.setStatus("paid"); adminPayRepo.save(p); r.put("success",true); r.put("message","Malipo ya Admin yamerekodiwa"); return r; }
@GetMapping("/stats/{ownerId}") public Map<String,Object> stats(@PathVariable Long ownerId){ Map<String,Object> r=new HashMap<>(); r.put("admins", userRepo.findByCreatedBy(ownerId).size()); r.put("totalMembers", memberRepo.count()); r.put("totalPayments", paymentRepo.count()); return r; }
}
