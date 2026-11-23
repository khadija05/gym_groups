package com.gym.controller;
import com.gym.model.User; import com.gym.repository.*;
import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @CrossOrigin(origins="*") @RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users; private final PaymentRepository payments; private final AttendanceRepository attendance;
    public AdminController(UserRepository u, PaymentRepository p, AttendanceRepository a){ this.users=u; this.payments=p; this.attendance=a; }
    @GetMapping("/report") public Map<String,Object> report(@RequestParam Long adminId){
        var admin=users.findById(adminId).orElseThrow();
        if(!"ADMIN".equals(admin.getRole()) && !"OWNER".equals(admin.getRole())) return Map.of("success",false,"message","Ni Admin tu");
        List<User> allMembers=users.findAll().stream().filter(x->"MEMBER".equals(x.getRole())).toList();
        List<User> myMembers=allMembers.stream().filter(m->{
            if(adminId.equals(m.getCreatedBy())) return true;
            if(admin.getGymGroup()!=null && !admin.getGymGroup().isEmpty() && admin.getGymGroup().equals(m.getGymGroup())) return true;
            if(admin.getGymGroup()==null || admin.getGymGroup().isEmpty()) return true;
            return false;
        }).toList();
        Set<Long> myIds=new HashSet<>(); myMembers.forEach(m->myIds.add(m.getId()));
        var myPayments=payments.findAll().stream().filter(p->p.getMemberId()!=null && myIds.contains(p.getMemberId())).toList();
        if(myPayments.isEmpty()) myPayments=payments.findAll();
        var myAttendance=attendance.findAll().stream().filter(a->{
            if(a.getMemberId()!=null && myIds.contains(a.getMemberId())) return true;
            for(User m: myMembers){ if(m.getName()!=null && m.getName().equalsIgnoreCase(a.getMemberName())) return true; if(m.getGymGroup()!=null && m.getGymGroup().equals(a.getGroupName())) return true; }
            return false;
        }).toList();
        Map<String,Object> r=new HashMap<>();
        r.put("success",true); r.put("adminName",admin.getName()); r.put("adminGroup",admin.getGymGroup());
        r.put("totalMembers",myMembers.size()); r.put("pendingMembers",myMembers.stream().filter(x->"pending".equals(x.getStatus())).count());
        r.put("activeMembers",myMembers.stream().filter(x->"active".equals(x.getStatus())).count());
        r.put("lockedMembers",myMembers.stream().filter(x->"locked".equals(x.getStatus())).count());
        r.put("totalPayments",myPayments.size()); r.put("totalAmount",myPayments.stream().mapToDouble(x->x.getAmount()==null?0:x.getAmount()).sum());
        r.put("totalAttendance",myAttendance.size());
        r.put("todayAttendance",myAttendance.stream().filter(a->a.getCheckIn()!=null && a.getCheckIn().toLocalDate().equals(java.time.LocalDate.now())).count());
        r.put("members",myMembers); r.put("payments",myPayments); r.put("attendance",myAttendance);
        return r;
    }
    @PostMapping("/register-member") public Map<String,Object> registerMember(@RequestBody Map<String,Object> body){
        Long adminId=Long.parseLong(body.get("adminId").toString()); var admin=users.findById(adminId).orElseThrow();
        if(!"ADMIN".equals(admin.getRole())) return Map.of("success",false,"message","Ni Admin tu");
        String username=body.get("username").toString(); if(users.findByUsername(username).isPresent()) return Map.of("success",false,"message","Username imetumika");
        User m=new User(); m.setName(body.get("name").toString()); m.setUsername(username); m.setPhone(body.getOrDefault("phone","").toString()); m.setPassword(body.get("password").toString());
        m.setRole("MEMBER"); m.setStatus("active"); m.setGymGroup(body.getOrDefault("gymGroup",admin.getGymGroup()==null?"":admin.getGymGroup()).toString()); m.setGoal(body.getOrDefault("goal","Kuwa na Afya").toString()); m.setPlan(body.getOrDefault("plan","KIDOGO").toString()); m.setCreatedBy(adminId); m.setExpiresAt(java.time.LocalDateTime.now().plusDays(30)); users.save(m);
        return Map.of("success",true,"message","Member amesajiliwa");
    }
}
