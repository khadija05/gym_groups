package com.gym.controller;
import com.gym.model.AdminPayment; import com.gym.repository.*;
import org.springframework.web.bind.annotation.*; import java.time.LocalDateTime; import java.time.temporal.ChronoUnit; import java.util.*;
@RestController @CrossOrigin(origins="*") @RequestMapping("/api/owner")
public class OwnerController {
    private final UserRepository users; private final PaymentRepository payments; private final AttendanceRepository attendance; private final MemberRepository members; private final AdminPaymentRepository adminPayments;
    public OwnerController(UserRepository u, PaymentRepository p, AttendanceRepository a, MemberRepository m, AdminPaymentRepository ap){ this.users=u; this.payments=p; this.attendance=a; this.members=m; this.adminPayments=ap; }
    @GetMapping("/report") public Map<String,Object> report(@RequestParam Long ownerId){
        var owner=users.findById(ownerId).orElseThrow(); if(!"OWNER".equals(owner.getRole())) return Map.of("success",false,"message","Ni Owner tu");
        Map<String,Object> r=new HashMap<>();
        r.put("totalUsers",users.count()); r.put("totalAdmins",users.countByRole("ADMIN")); r.put("totalMembers",users.countByRole("MEMBER"));
        r.put("pendingMembers",users.findAll().stream().filter(x->"pending".equals(x.getStatus())).count());
        r.put("activeMembers",users.findAll().stream().filter(x->"active".equals(x.getStatus())).count());
        r.put("totalPayments",payments.count()); r.put("totalAmount",payments.findAll().stream().mapToDouble(x->x.getAmount()==null?0:x.getAmount()).sum());
        r.put("totalAttendance",attendance.count()); r.put("allUsers",users.findAll()); r.put("allPayments",payments.findAll()); r.put("allAttendance",attendance.findAll());
        return r;
    }
    @GetMapping("/admins-report") public Map<String,Object> adminsReport(@RequestParam Long ownerId){
        var owner=users.findById(ownerId).orElseThrow(); if(!"OWNER".equals(owner.getRole())) return Map.of("success",false);
        var admins=users.findAll().stream().filter(u->"ADMIN".equals(u.getRole())).toList();
        var all=new ArrayList<Map<String,Object>>();
        for(var a: admins){
            Map<String,Object> m=new HashMap<>(); m.put("id",a.getId()); m.put("name",a.getName()); m.put("username",a.getUsername()); m.put("phone",a.getPhone()); m.put("group",a.getGymGroup()); m.put("status",a.getStatus()); m.put("subscriptionStatus",a.getSubscriptionStatus()); m.put("monthlyFee",a.getMonthlyFee()); m.put("expiresAt",a.getSubscriptionExpiresAt()!=null?a.getSubscriptionExpiresAt().toString():"");
            long daysLeft=a.getSubscriptionExpiresAt()!=null?ChronoUnit.DAYS.between(LocalDateTime.now(),a.getSubscriptionExpiresAt()):0; m.put("daysLeft",daysLeft); m.put("isLocked","locked".equals(a.getStatus()));
            long cnt=users.findAll().stream().filter(x->"MEMBER".equals(x.getRole()) && a.getId().equals(x.getCreatedBy())).count(); m.put("membersCount",cnt);
            m.put("payments",adminPayments.findByAdminId(a.getId())); all.add(m);
        }
        return Map.of("success",true,"admins",all,"pendingPayments",adminPayments.findByStatus("pending"));
    }
    @PostMapping("/approve-admin-payment/{paymentId}") public Map<String,Object> approveAdminPayment(@PathVariable Long paymentId, @RequestParam Long ownerId){
        var owner=users.findById(ownerId).orElseThrow(); if(!"OWNER".equals(owner.getRole())) return Map.of("success",false,"message","Ni Owner tu");
        var pay=adminPayments.findById(paymentId).orElseThrow(); pay.setStatus("approved"); pay.setApprovedAt(LocalDateTime.now()); adminPayments.save(pay);
        var admin=users.findById(pay.getAdminId()).orElseThrow(); admin.setStatus("active"); admin.setSubscriptionStatus("active"); admin.setSubscriptionExpiresAt(LocalDateTime.now().plusDays(30)); users.save(admin);
        users.findAll().stream().filter(m->"MEMBER".equals(m.getRole()) && admin.getId().equals(m.getCreatedBy())).forEach(m->{ m.setStatus("active"); users.save(m); });
        return Map.of("success",true,"message","Admin amefunguliwa - siku 30 zingine");
    }
    @PostMapping("/admin-pay") public Map<String,Object> adminPay(@RequestBody Map<String,Object> b){
        Long adminId=Long.parseLong(b.get("adminId").toString()); var admin=users.findById(adminId).orElseThrow();
        AdminPayment p=new AdminPayment(); p.setAdminId(adminId); p.setAdminName(admin.getName()); p.setGroupName(admin.getGymGroup()); p.setAmount(admin.getMonthlyFee()); p.setMonth(java.time.LocalDate.now().toString().substring(0,7)); p.setReference(b.getOrDefault("reference","").toString()); p.setStatus("pending"); adminPayments.save(p);
        return Map.of("success",true,"message","Malipo yametumwa. Subiri Owner akuthibitishe.");
    }
}
