package com.gym.controller;
import com.gym.model.User;
import com.gym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.LocalDateTime;
@RestController @RequestMapping("/api/auth")
public class AuthController {
@Autowired UserRepository userRepo;

private Map<String,Object> toMap(User u){
Map<String,Object> res=new HashMap<>();
res.put("success",true);
res.put("id",u.getId());
res.put("name",u.getName());
res.put("username",u.getUsername());
res.put("phone",u.getPhone()==null?"":u.getPhone());
res.put("role",u.getRole());
res.put("status",u.getStatus());
res.put("gymGroup",u.getGymGroup()==null?"":u.getGymGroup());
res.put("goal",u.getGoal()==null?"":u.getGoal());
res.put("plan",u.getPlan()==null?"":u.getPlan());
res.put("monthlyFee",u.getMonthlyFee()==null?0:u.getMonthlyFee());
res.put("createdBy",u.getCreatedBy()==null?0:u.getCreatedBy());
res.put("subscriptionStatus",u.getSubscriptionStatus()==null?"":u.getSubscriptionStatus());
res.put("expiresAt",u.getExpiresAt()==null?"":u.getExpiresAt().toString());
res.put("subscriptionExpiresAt",u.getSubscriptionExpiresAt()==null?"":u.getSubscriptionExpiresAt().toString());
return res;
}

@PostMapping("/register-owner")
public Map<String,Object> registerOwner(@RequestBody Map<String,String> b){
Map<String,Object> res=new HashMap<>();
if(b.get("username")==null||b.get("username").isBlank()){res.put("success",false);res.put("message","Username inahitajika");return res;}
if(userRepo.existsByUsername(b.get("username"))){res.put("success",false);res.put("message","Username tayari inatumika - jaribu nyingine");return res;}
if(b.get("phone")!=null&&!b.get("phone").isBlank()&&userRepo.existsByPhone(b.get("phone"))){res.put("success",false);res.put("message","Simu tayari inatumika - jaribu simu nyingine");return res;}
User u=new User(); u.setName(b.get("name")); u.setUsername(b.get("username")); u.setPhone(b.get("phone")); u.setPassword(b.get("password")); u.setRole("OWNER"); u.setStatus("active"); u.setGymGroup(b.getOrDefault("gymGroup","")); u.setCreatedAt(LocalDateTime.now()); userRepo.save(u);
Map<String,Object> out=toMap(u); out.put("message","Owner amesajiliwa"); return out;
}

@PostMapping("/register-admin")
public Map<String,Object> registerAdmin(@RequestBody Map<String,Object> b){
Map<String,Object> res=new HashMap<>();
String username=(String)b.get("username"); String phone=(String)b.get("phone");
if(username==null||username.isBlank()){res.put("success",false);res.put("message","Username inahitajika");return res;}
if(userRepo.existsByUsername(username)){res.put("success",false);res.put("message","Username tayari inatumika - jaribu nyingine");return res;}
if(phone!=null&&!phone.isBlank()&&userRepo.existsByPhone(phone)){res.put("success",false);res.put("message","Simu tayari inatumika - jaribu simu nyingine");return res;}
Long ownerId=null; try{ Object o=b.get("ownerId"); if(o!=null) ownerId=Long.parseLong(o.toString()); }catch(Exception e){}
if(ownerId==null){res.put("success",false);res.put("message","Owner ID inahitajika - ingia tena kama Owner");return res;}
Optional<User> owner=userRepo.findById(ownerId); if(owner.isEmpty()||!"OWNER".equals(owner.get().getRole())){res.put("success",false);res.put("message","Owner hajapatikana - ingia tena");return res;}
User u=new User(); u.setName((String)b.get("name")); u.setUsername(username); u.setPhone(phone); u.setPassword((String)b.get("password")); u.setRole("ADMIN"); u.setStatus("active"); u.setGymGroup((String)b.getOrDefault("gymGroup",owner.get().getGymGroup())); u.setCreatedBy(ownerId); u.setCreatedAt(LocalDateTime.now());
Object fee=b.get("monthlyFee"); if(fee!=null) try{u.setMonthlyFee(Double.parseDouble(fee.toString()));}catch(Exception e){}
u.setSubscriptionStatus("active"); u.setSubscriptionExpiresAt(LocalDateTime.now().plusMonths(1));
userRepo.save(u); Map<String,Object> out=toMap(u); out.put("message","Admin amesajiliwa"); return out;
}

@PostMapping("/register-member")
public Map<String,Object> registerMember(@RequestBody Map<String,Object> b){
Map<String,Object> res=new HashMap<>();
String username=(String)b.get("username"); String phone=(String)b.get("phone");
if(username!=null&&!username.isBlank()&&userRepo.existsByUsername(username)){res.put("success",false);res.put("message","Username tayari inatumika - jaribu nyingine");return res;}
if(phone!=null&&!phone.isBlank()&&userRepo.existsByPhone(phone)){res.put("success",false);res.put("message","Simu tayari inatumika - jaribu simu nyingine");return res;}
User u=new User(); u.setName((String)b.get("name")); u.setUsername(username); u.setPhone(phone); u.setPassword((String)b.get("password")); u.setRole("MEMBER"); u.setStatus("pending"); u.setGymGroup((String)b.get("gymGroup")); u.setGoal((String)b.get("goal")); u.setPlan((String)b.getOrDefault("plan","JARIBIO")); u.setCreatedAt(LocalDateTime.now());
Object cb=b.get("createdBy"); if(cb!=null) try{u.setCreatedBy(Long.parseLong(cb.toString()));}catch(Exception e){}
userRepo.save(u); Map<String,Object> out=toMap(u); out.put("message","Member amesajiliwa, subiri akubaliwe"); return out;
}

@PostMapping("/login")
public Map<String,Object> login(@RequestBody Map<String,String> b){
Map<String,Object> res=new HashMap<>();
Optional<User> opt=userRepo.findByUsername(b.get("username"));
if(opt.isEmpty()){res.put("success",false);res.put("message","Username haipo");return res;}
User u=opt.get(); if(!u.getPassword().equals(b.get("password"))){res.put("success",false);res.put("message","Password sio sahihi");return res;}
if("locked".equals(u.getStatus())){res.put("success",false);res.put("message","Akaunti imefungwa - wasiliana na Owner");return res;}
Map<String,Object> out=toMap(u); out.put("message","Karibu "+u.getName()); return out;
}
}
