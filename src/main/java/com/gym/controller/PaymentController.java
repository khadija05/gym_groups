
package com.gym.controller;

import com.gym.model.Payment;
import com.gym.model.Gym;
import com.gym.model.Member;
import com.gym.repository.PaymentRepo;
import com.gym.repository.GymRepo;
import com.gym.repository.MemberRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

  private final PaymentRepo payments;
  private final GymRepo gyms;
  private final MemberRepo members;

  public PaymentController(PaymentRepo p, GymRepo g, MemberRepo m){
    this.payments = p; this.gyms = g; this.members = m;
  }

  // MALIPO YOTE - KWA OWNER: GYM ZOTE
  @GetMapping
  public ResponseEntity<?> getAll(){
    try{
      var list = new ArrayList<Map<String,Object>>();
      for(var p : payments.findAll()){
        Map<String,Object> m = new HashMap<>();
        m.put("id", p.getId());
        m.put("phone", p.getPhone());
        m.put("plan", p.getPlan());
        m.put("reference", p.getReference());
        m.put("amount", p.getAmount());
        m.put("status", p.getStatus());
        m.put("gymId", p.getGymId());
        m.put("gym_id", p.getGymId());
        m.put("memberId", p.getMemberId());
        m.put("gymName", p.getGymName());
        m.put("adminName", p.getAdminName());
        m.put("adminPhone", p.getAdminPhone());
        m.put("createdAt", String.valueOf(p.getCreatedAt()));
        m.put("created_at", String.valueOf(p.getCreatedAt()));

        // Kama Payment haina jina la Gym, leta kutoka Gym
        if((p.getGymName() == null || p.getGymName().isEmpty()) && p.getGymId() != null){
          try{
            var g = gyms.findById(p.getGymId());
            if(g.isPresent()){
              m.put("gymName", g.get().getName());
              m.put("adminName", g.get().getAdminName());
              m.put("adminPhone", g.get().getAdminPhone());
            }
          }catch(Exception ignored){}
        }
        list.add(m);
      }
      Collections.reverse(list);
      return ResponseEntity.ok(list);
    }catch(Exception e){
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("error", String.valueOf(e.getMessage())));
    }
  }

  // VIEW - MALIPO MOJA
  @GetMapping("/{id}")
  public ResponseEntity<?> getOne(@PathVariable Long id){
    var opt = payments.findById(id);
    if(opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error","Malipo hayajapatikana"));
    var p = opt.get();
    Map<String,Object> m = new HashMap<>();
    m.put("id", p.getId());
    m.put("phone", p.getPhone());
    m.put("plan", p.getPlan());
    m.put("reference", p.getReference());
    m.put("amount", p.getAmount());
    m.put("status", p.getStatus());
    m.put("gymId", p.getGymId());
    m.put("memberId", p.getMemberId());
    m.put("gymName", p.getGymName());
    m.put("adminName", p.getAdminName());
    m.put("createdAt", String.valueOf(p.getCreatedAt()));
    return ResponseEntity.ok(m);
  }

  // ===== HII NDIO YA KUTHIBITISHA - FUNGUA GYM + ONGEZA SIKU 30 =====
  @PutMapping("/{id}/status")
  public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String,String> b){
    var opt = payments.findById(id);
    if(opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error","Hayajapatikana"));
    var p = opt.get();
    String newStatus = b.getOrDefault("status","PENDING");
    p.setStatus(newStatus);
    payments.save(p);

    // 1. KAMA OWNER AMETHIBITISHA MALIPO YA GYM - FUNGUA GYM + SIKU 30
    if("APPROVED".equalsIgnoreCase(newStatus) && p.getGymId() != null){
      var gOpt = gyms.findById(p.getGymId());
      if(gOpt.isPresent()){
        Gym g = gOpt.get();
        g.setStatus("ACTIVE");
        g.setActive(true);
        LocalDate now = LocalDate.now();
        LocalDate currentEnd = g.getPackageEndDate();
        if(currentEnd != null && currentEnd.isAfter(now)){
          g.setPackageEndDate(currentEnd.plusDays(30));
        } else {
          g.setPackageEndDate(now.plusDays(30));
        }
        gyms.save(g);
        // Kama ni malipo ya Member pia, muongezee siku 30
        if(p.getMemberId() != null){
          try{
            var mOpt = members.findById(p.getMemberId());
            if(mOpt.isPresent()){
              Member mm = mOpt.get();
              LocalDate mEnd = mm.getEndDate();
              if(mEnd != null && mEnd.isAfter(now)){
                mm.setEndDate(mEnd.plusDays(30));
              } else {
                mm.setEndDate(now.plusDays(30));
              }
              mm.setStatus("ACTIVE");
              members.save(mm);
            }
          }catch(Exception ignored){}
        }
        return ResponseEntity.ok(Map.of("message","✅ Malipo yamethibitishwa! Gym " + g.getName() + " imefunguliwa kwa siku 30. Inaisha: " + g.getPackageEndDate()));
      }
    }

    if("REJECTED".equalsIgnoreCase(newStatus)){
      return ResponseEntity.ok(Map.of("message","❌ Malipo yamekataliwa"));
    }
    return ResponseEntity.ok(Map.of("message","Status imebadilishwa kuwa " + newStatus));
  }

  // TENGENEZA MALIPO MPYA - Admin / Member analipa
  @PostMapping
  public ResponseEntity<?> create(@RequestBody Map<String,Object> b){
    try{
      Payment p = new Payment();
      p.setPhone(String.valueOf(b.getOrDefault("phone","")));
      p.setPlan(String.valueOf(b.getOrDefault("plan","Mwezi 1")));
      p.setReference(String.valueOf(b.getOrDefault("reference","")));
      try{ p.setAmount(Integer.valueOf(String.valueOf(b.getOrDefault("amount","0")))); }catch(Exception e){ p.setAmount(0); }
      p.setStatus(String.valueOf(b.getOrDefault("status","PENDING")));
      p.setGymName(String.valueOf(b.getOrDefault("gymName","")));
      p.setAdminName(String.valueOf(b.getOrDefault("adminName","")));
      p.setAdminPhone(String.valueOf(b.getOrDefault("adminPhone", b.getOrDefault("phone",""))));
      if(b.get("gymId") != null && !String.valueOf(b.get("gymId")).isEmpty() && !String.valueOf(b.get("gymId")).equals("null")){
        p.setGymId(Long.valueOf(String.valueOf(b.get("gymId"))));
      }
      if(b.get("memberId") != null && !String.valueOf(b.get("memberId")).isEmpty() && !String.valueOf(b.get("memberId")).equals("null")){
        p.setMemberId(Long.valueOf(String.valueOf(b.get("memberId"))));
      }
      // Kama gymId ipo lakini jina halipo, chukua kutoka Gym
      if(p.getGymId() != null && (p.getGymName() == null || p.getGymName().isEmpty() || p.getGymName().equals("null"))){
        try{
          var g = gyms.findById(p.getGymId());
          if(g.isPresent()){
            p.setGymName(g.get().getName());
            p.setAdminName(g.get().getAdminName());
            p.setAdminPhone(g.get().getAdminPhone());
          }
        }catch(Exception ignored){}
      }
      payments.save(p);
      return ResponseEntity.ok(Map.of("message","✅ Malipo yamehifadhiwa, yanasubiri kuthibitishwa na Owner", "id", p.getId()));
    }catch(Exception e){
      e.printStackTrace();
      return ResponseEntity.status(500).body(Map.of("error", String.valueOf(e.getMessage())));
    }
  }
}
