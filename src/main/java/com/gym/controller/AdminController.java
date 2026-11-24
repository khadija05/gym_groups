package com.gym.controller;
import com.gym.model.*;
import com.gym.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/admin")
public class AdminController {
@Autowired MemberRepository memberRepo; @Autowired PaymentRepository paymentRepo; @Autowired AttendanceRepository attRepo; @Autowired UserRepository userRepo;
@GetMapping("/members/{adminId}") public List<Member> members(@PathVariable Long adminId){ return memberRepo.findByCreatedBy(adminId); }
@GetMapping("/users/{adminId}") public List<User> users(@PathVariable Long adminId){ return userRepo.findByRoleAndCreatedBy("MEMBER", adminId); }
@PostMapping("/add-member") public Map<String,Object> addMember(@RequestBody Map<String,Object> b){ Map<String,Object> r=new HashMap<>(); Member m=new Member(); m.setName((String)b.get("name")); m.setPhone((String)b.get("phone")); m.setGymGroup((String)b.get("gymGroup")); m.setGoal((String)b.get("goal")); m.setPlan((String)b.getOrDefault("plan","JARIBIO")); m.setStatus("active"); try{m.setCreatedBy(Long.parseLong(b.get("createdBy").toString()));}catch(Exception e){} try{m.setMonthlyFee(Double.parseDouble(b.getOrDefault("monthlyFee","50000").toString()));}catch(Exception e){} memberRepo.save(m); r.put("success",true); r.put("message","Member ameongezwa"); return r; }
@GetMapping("/payments/{adminId}") public List<Payment> payments(@PathVariable Long adminId){ return paymentRepo.findByUserId(adminId); }
@PostMapping("/add-payment") public Map<String,Object> addPayment(@RequestBody Map<String,Object> b){ Map<String,Object> r=new HashMap<>(); Payment p=new Payment(); p.setMemberName((String)b.get("memberName")); try{p.setMemberId(Long.parseLong(b.get("memberId").toString()));}catch(Exception e){} try{p.setUserId(Long.parseLong(b.get("userId").toString()));}catch(Exception e){} try{p.setAmount(Double.parseDouble(b.getOrDefault("amount","0").toString()));}catch(Exception e){} p.setMethod((String)b.getOrDefault("method","Cash")); p.setDate((String)b.getOrDefault("date","")); p.setStatus("paid"); paymentRepo.save(p); r.put("success",true); r.put("message","Malipo yamerekodiwa"); return r; }
@PostMapping("/attendance") public Map<String,Object> att(@RequestBody Map<String,Object> b){ Map<String,Object> r=new HashMap<>(); Attendance a=new Attendance(); a.setMemberName((String)b.get("memberName")); try{a.setMemberId(Long.parseLong(b.get("memberId").toString()));}catch(Exception e){} a.setDate((String)b.get("date")); a.setStatus("present"); attRepo.save(a); r.put("success",true); return r; }
@GetMapping("/attendance/{date}") public List<Attendance> attByDate(@PathVariable String date){ return attRepo.findByDate(date); }
}
