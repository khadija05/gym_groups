package com.gym.controller;
import com.gym.model.*;
import com.gym.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api")
public class GymController {
@Autowired MemberRepository memberRepo; @Autowired PaymentRepository paymentRepo;
@GetMapping("/health") public Map<String,Object> health(){ Map<String,Object> r=new HashMap<>(); r.put("status","ok"); r.put("message","Gym API inafanya kazi"); return r; }
@GetMapping("/members") public List<Member> allMembers(){ return memberRepo.findAll(); }
@GetMapping("/") public Map<String,Object> home(){ Map<String,Object> r=new HashMap<>(); r.put("status","ok"); r.put("app","gym-zanzibar"); return r; }
}
