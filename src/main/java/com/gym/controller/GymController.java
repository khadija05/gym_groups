package com.gym.controller;
import com.gym.model.*; import com.gym.repo.*;
import jakarta.annotation.PostConstruct; import jakarta.servlet.http.HttpServletRequest; import org.springframework.http.ResponseEntity; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import java.time.LocalDate; import java.util.*;
@RestController @RequestMapping("/api/gym")
public class GymController {
  private final UserRepo users; private final GymRepo gyms; private final MemberRepo members; private final PaymentRepo payments; private final AttendanceRepo attendance; private final WorkoutRepo workouts; private final PasswordEncoder encoder;
  public GymController(UserRepo u,GymRepo g,MemberRepo m,PaymentRepo p,AttendanceRepo a,WorkoutRepo w,PasswordEncoder e){this.users=u;this.gyms=g;this.members=m;this.payments=p;this.attendance=a;this.workouts=w;this.encoder=e;}

  // Vikundi 4 vya default - vinaundwa mara ya kwanza tu
  @PostConstruct public void seed(){
    if(gyms.count()>0) return;
    String[][] d = {{"Gym Stone Town - Jenga Mwili","Kocha Juma","Jumatatu - Jumamosi","5:00 Asubuhi - 9:00 Jioni","30000","25","🏋️","Kujenga misuli","#FFD400"},{"Gym Mjini - Punguza Uzito","Kocha Amina","Jumatatu, Jumatano, Ijumaa","6:00 Asubuhi • 6:00 Jioni","30000","18","🔥","Punguza uzito","#FF6B00"},{"Gym Forodhani - Wanawake Tu","Kocha Fatma","Jumanne, Alhamisi, Jumamosi","6:00 Jioni - 8:00 Jioni","25000","12","🧘","Wanawake tu","#FF69B4"},{"Gym Mjini Magharibi - Wanaume Tu","Kocha Ali","Kila Siku","5:00 Asubuhi - 10:00 Jioni","30000","22","💪","Wanaume tu","#00D4FF"}};
    for(String[] x: d){ Gym g=new Gym(); g.setName(x[0]); g.setCoach(x[1]); g.setDays(x[2]); g.setTimeSlot(x[3]); g.setPrice(Integer.valueOf(x[4])); g.setMembersCount(Integer.valueOf(x[5])); g.setEmoji(x[6]); g.setDescription(x[7]); g.setColor(x[8]); gyms.save(g); }
  }

  @GetMapping("/groups") public List<Gym> groups(){ return gyms.findAll(); }

  @PostMapping("/attendance") public ResponseEntity<?> attend(@RequestBody Map<String,String> b){
    Attendance a=new Attendance(); a.setMemberName(b.get("memberName")); a.setGroupName(b.get("groupName")); attendance.save(a);
    return ResponseEntity.ok(Map.of("message","Amehudhuria"));
  }
  @GetMapping("/attendance/today") public Map<String,Object> today(){ return Map.of("count", attendance.countByDate(LocalDate.now()), "list", attendance.findByDateOrderByIdDesc(LocalDate.now())); }
}
