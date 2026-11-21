package com.gym.controller;
import com.gym.model.User; import com.gym.repository.UserRepository;
import org.springframework.web.bind.annotation.*; import java.util.*; import java.time.LocalDateTime;
@RestController @RequestMapping("/api/auth") @CrossOrigin("*")
public class AuthController {
  private final UserRepository users; public AuthController(UserRepository u){ this.users=u; }
  private int daysForPlan(String p){
    if(p==null) return 3;
    if(p.equals("JARIBIO")) return 3;
    if(p.equals("KIDOGO")) return 30;
    if(p.equals("KUBWA")) return 365;
    return 30;
  }
  @PostMapping("/login") public Map<String,Object> login(@RequestBody Map<String,String> b){
    String ph=b.get("phone"), pw=b.get("password"), un=b.get("username");
    String key=(ph!=null&&!ph.isBlank())?ph:un;
    if("khadija".equals(key) && "khadija123".equals(pw)) return Map.of("success",true,"name","Khadija","username","khadija","phone","0887387001","role","ADMIN","plan","SPECIAL","status","active","approvedByKhadija",true);
    Optional<User> opt = (ph!=null&&!ph.isBlank()?users.findByPhone(ph):Optional.<User>empty()).or(()->un!=null?users.findByUsername(un):Optional.empty());
    if(opt.isEmpty()) return Map.of("error","NOT_FOUND","message","Akaunti haipo. Jisajili kwanza.");
    User u=opt.get();
    if(!u.getPassword().equals(pw)) return Map.of("error","WRONG_PASSWORD","message","Password si sahihi.");
    if(Boolean.TRUE.equals(u.getLocked())) return Map.of("error","LOCKED","message","Akaunti imefungwa. Lipa ili ufunguliwe.");
    if(!Boolean.TRUE.equals(u.getApprovedByKhadija())) return Map.of("error","NOT_APPROVED","message","Bado haijathibitishwa na Mmiliki. Subiri kidogo.");
    if(u.getExpiresAt()!=null && LocalDateTime.now().isAfter(u.getExpiresAt())){ u.setStatus("expired"); u.setLocked(true); users.save(u); return Map.of("error","EXPIRED","message","Muda wako umeisha! Lipa ili uendelee."); }
    if(!"active".equals(u.getStatus())) return Map.of("error","NOT_ACTIVE","message","Akaunti si active. Lipa kwanza.");
    // FIX: HashMap badala ya Map.of ya 11 pairs
    Map<String,Object> response = new HashMap<>();
    response.put("success", true);
    response.put("id", u.getId());
    response.put("username", u.getUsername());
    response.put("name", u.getName());
    response.put("phone", u.getPhone());
    response.put("role", u.getRole());
    response.put("plan", u.getPlan());
    response.put("status", u.getStatus());
    response.put("gymGroup", u.getGymGroup()==null?"":u.getGymGroup());
    response.put("approvedByKhadija", true);
    response.put("expiresAt", u.getExpiresAt()==null?"":u.getExpiresAt().toString());
    return response;
  }
  @PostMapping("/register") public Map<String,Object> register(@RequestBody Map<String,String> m){
    String phone=m.get("phone"); String username=m.get("username");
    if(phone!=null&&!phone.isEmpty()&&users.findByPhone(phone).isPresent()) return Map.of("error","ALREADY_REGISTERED","message","Namba hii imeshajisajiliwa. INGIA tu.");
    if(username!=null&&users.findByUsername(username).isPresent()) return Map.of("error","ALREADY_REGISTERED","message","Username hii imeshachukuliwa.");
    User u=new User();
    String full=m.get("fullName"); if(full==null) full=m.get("name"); if(full==null||full.isBlank()) full=username;
    u.setName(full); u.setUsername(username); u.setPhone(phone); u.setPassword(m.get("password"));
    String role=m.get("role"); if(role==null) role="MEMBER"; u.setRole(role);
    String plan=m.get("plan"); if(plan==null) plan="JARIBIO"; u.setPlan(plan);
    u.setGymGroup(m.get("gymGroup")); u.setGoal(m.get("goal"));
    try{ if(m.get("startWeight")!=null) u.setStartWeight(Double.parseDouble(m.get("startWeight"))); }catch(Exception e){}
    try{ if(m.get("currentWeight")!=null) u.setCurrentWeight(Double.parseDouble(m.get("currentWeight"))); }catch(Exception e){}
    try{ if(m.get("goalWeight")!=null) u.setGoalWeight(Double.parseDouble(m.get("goalWeight"))); }catch(Exception e){}
    u.setExpiresAt(LocalDateTime.now().plusDays(daysForPlan(plan)));
    u.setStatus("pending"); u.setApprovedByKhadija(false);
    String txn=m.get("paymentTxn"); if(txn!=null&&!txn.isBlank()) u.setStatus("waiting-payment");
    User s=users.save(u); return Map.of("success",true,"id",s.getId(),"username",s.getUsername());
  }
  @PostMapping("/pay") public Map<String,Object> pay(@RequestBody Map<String,String> b){
    Optional<User> opt=users.findByPhone(b.get("phone")); if(opt.isEmpty()) return Map.of("error","NOT_FOUND","message","Akaunti haipo. Jisajili kwanza.");
    User u=opt.get(); u.setPlan(b.getOrDefault("plan","KIDOGO")); u.setStatus("waiting-payment"); users.save(u); return Map.of("success",true);
  }
  @PutMapping("/approve/{id}") public User approve(@PathVariable Long id){ User u=users.findById(id).orElseThrow(); u.setApprovedByKhadija(true); u.setStatus("active"); u.setLocked(false); u.setExpiresAt(LocalDateTime.now().plusDays(daysForPlan(u.getPlan()))); return users.save(u); }
  @PutMapping("/lock/{id}") public User lock(@PathVariable Long id){ User u=users.findById(id).orElseThrow(); u.setLocked(true); u.setStatus("locked"); return users.save(u); }
  @PutMapping("/progress/{id}") public User progress(@PathVariable Long id, @RequestBody Map<String,String> b){ User u=users.findById(id).orElseThrow(); try{ if(b.get("currentWeight")!=null) u.setCurrentWeight(Double.parseDouble(b.get("currentWeight"))); }catch(Exception e){} return users.save(u); }
  @GetMapping("/all") public List<User> all(){ return users.findAll(); }
}
