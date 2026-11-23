package com.gym.controller;
import com.gym.model.User; import com.gym.repository.UserRepository;
import org.springframework.web.bind.annotation.*; import java.time.LocalDateTime; import java.util.*;
@RestController @CrossOrigin(origins="*") @RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    public AuthController(UserRepository u){this.users=u;}
    @PostMapping("/register-owner")
    public Map<String,Object> registerOwner(@RequestBody User body){
        Map<String,Object> res=new HashMap<>();
        if(users.existsByRole("OWNER")){ res.put("success",false); res.put("message","Owner tayari yupo. Ingia na akaunti yako."); return res; }
        if(users.findByUsername(body.getUsername()).isPresent()){ res.put("success",false); res.put("message","Username imetumika."); return res; }
        body.setRole("OWNER"); body.setStatus("active"); body.setPlan("KUBWA"); body.setExpiresAt(LocalDateTime.now().plusYears(10));
        users.save(body);
        res.put("success",true); res.put("message","Owner ameundwa."); res.put("id",body.getId()); res.put("name",body.getName()); res.put("username",body.getUsername()); res.put("role","OWNER"); res.put("status","active");
        return res;
    }
    @PostMapping("/register")
    public Map<String,Object> register(@RequestBody User body){
        Map<String,Object> res=new HashMap<>();
        if(users.findByUsername(body.getUsername()).isPresent()){ res.put("success",false); res.put("message","Username imetumika."); return res; }
        body.setRole("MEMBER"); body.setStatus("pending");
        if("JARIBIO".equals(body.getPlan())) body.setExpiresAt(LocalDateTime.now().plusDays(3));
        if("KIDOGO".equals(body.getPlan())) body.setExpiresAt(LocalDateTime.now().plusDays(30));
        if("KUBWA".equals(body.getPlan())) body.setExpiresAt(LocalDateTime.now().plusDays(365));
        users.save(body); res.put("success",true); res.put("message","Umejisajili. Subiri kukubaliwa."); return res;
    }
    @PostMapping("/register-admin")
    public Map<String,Object> registerAdmin(@RequestBody Map<String,Object> body){
        Map<String,Object> res=new HashMap<>();
        Long ownerId=Long.parseLong(body.get("ownerId").toString());
        Optional<User> owner=users.findById(ownerId);
        if(owner.isEmpty()||!"OWNER".equals(owner.get().getRole())){ res.put("success",false); res.put("message","Ni Owner tu anaweza kusajili Admin."); return res; }
        String username=body.get("username").toString();
        if(users.findByUsername(username).isPresent()){ res.put("success",false); res.put("message","Username imetumika."); return res; }
        User admin=new User(); admin.setName(body.get("name").toString()); admin.setUsername(username);
        admin.setPhone(body.getOrDefault("phone","").toString()); admin.setPassword(body.get("password").toString());
        admin.setRole("ADMIN"); admin.setStatus("active"); admin.setCreatedBy(ownerId);
        admin.setGymGroup(body.getOrDefault("gymGroup","").toString());
        try{ admin.setMonthlyFee(Double.parseDouble(body.getOrDefault("monthlyFee","50000").toString())); }catch(Exception e){ admin.setMonthlyFee(50000.0); }
        admin.setSubscriptionExpiresAt(LocalDateTime.now().plusDays(30)); admin.setSubscriptionStatus("active");
        admin.setExpiresAt(LocalDateTime.now().plusYears(5));
        users.save(admin); res.put("success",true); res.put("message","Admin ameundwa na Owner."); return res;
    }
    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody Map<String,String> body){
        Map<String,Object> res=new HashMap<>();
        String u=body.getOrDefault("username",body.get("phone")); String p=body.get("password");
        Optional<User> found=users.findByUsername(u); if(found.isEmpty()) found=users.findByPhone(u);
        if(found.isEmpty()){ res.put("success",false); res.put("error","NOT_FOUND"); res.put("message","Akaunti haipo."); return res; }
        User user=found.get();
        if(!user.getPassword().equals(p)){ res.put("success",false); res.put("error","WRONG_PASSWORD"); return res; }
        if("pending".equals(user.getStatus())){ res.put("success",false); res.put("error","NOT_APPROVED"); return res; }
        if("locked".equals(user.getStatus())){
            if("ADMIN".equals(user.getRole())){ res.put("success",false); res.put("error","ADMIN_LOCKED"); res.put("message","Mfumo umejifunga. Lipa ada ya mwezi kwa Owner."); }
            else res.put("error","LOCKED");
            return res;
        }
        if(user.getExpiresAt()!=null && user.getExpiresAt().isBefore(LocalDateTime.now()) && !"OWNER".equals(user.getRole())){ res.put("success",false); res.put("error","EXPIRED"); return res; }
        res.put("success",true); res.put("id",user.getId()); res.put("name",user.getName()); res.put("username",user.getUsername()); res.put("phone",user.getPhone()); res.put("role",user.getRole()); res.put("status",user.getStatus()); res.put("plan",user.getPlan()); res.put("gymGroup",user.getGymGroup()); res.put("monthlyFee",user.getMonthlyFee()); res.put("subscriptionExpiresAt",user.getSubscriptionExpiresAt()!=null?user.getSubscriptionExpiresAt().toString():"");
        return res;
    }
    @GetMapping("/all") public List<User> all(){ return users.findAll(); }
    @GetMapping("/admins") public List<User> admins(){ return users.findAll().stream().filter(x->"ADMIN".equals(x.getRole())).toList(); }
    @PutMapping("/approve/{id}") public Map<String,Object> approve(@PathVariable Long id){ User u=users.findById(id).orElseThrow(); u.setStatus("active"); users.save(u); return Map.of("success",true); }
    @PutMapping("/lock/{id}") public Map<String,Object> lock(@PathVariable Long id){ User u=users.findById(id).orElseThrow(); u.setStatus("locked"); users.save(u); return Map.of("success",true); }
    @DeleteMapping("/{id}") public Map<String,Object> delete(@PathVariable Long id){ users.deleteById(id); return Map.of("success",true); }
    @PostMapping("/pay") public Map<String,Object> pay(@RequestBody Map<String,String> b){
        Optional<User> u=users.findByPhone(b.get("phone")); if(u.isEmpty()) return Map.of("success",false,"message","Mteja hayupo");
        User user=u.get(); if("KIDOGO".equals(b.get("plan"))) user.setExpiresAt(LocalDateTime.now().plusDays(30)); if("KUBWA".equals(b.get("plan"))) user.setExpiresAt(LocalDateTime.now().plusDays(365)); user.setPlan(b.get("plan")); user.setStatus("pending"); users.save(user); return Map.of("success",true);
    }
}
