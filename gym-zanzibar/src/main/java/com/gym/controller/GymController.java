
package com.gym.controller;
import com.gym.model.*; import com.gym.repository.*;
import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/gym") @CrossOrigin("*")
public class GymController {
  private final GymGroupRepository groups; private final AttendanceRepository attends;
  public GymController(GymGroupRepository g, AttendanceRepository a){ this.groups=g; this.attends=a; }
  @GetMapping("/groups") public List<GymGroup> groups(){
    if(groups.count()==0){
      groups.saveAll(List.of(
        mk("Gym Stone Town - Jenga Mwili","Kocha Juma","Jumatatu - Jumamosi","5:00 Asubuhi - 9:00 Jioni",30000,25,"🏋️","Kujenga misuli • Vifaa vyote • Kocha binafsi","#FFD400"),
        mk("Gym Mjini - Punguza Uzito","Kocha Amina","Jumatatu, Jumatano, Ijumaa","6:00 Asubuhi • 6:00 Jioni",30000,18,"🔥","Punguza uzito • Cardio • Mpango wa chakula","#FF6B00"),
        mk("Gym Forodhani - Wanawake Tu","Kocha Fatma","Jumanne, Alhamisi, Jumamosi","6:00 Jioni - 8:00 Jioni",25000,12,"🧘","Wanawake tu • Yoga • Aerobics • Faragha","#FF69B4"),
        mk("Gym Mjini Magharibi - Wanaume Tu","Kocha Ali","Kila Siku","5:00 Asubuhi - 10:00 Jioni",30000,22,"💪","Wanaume tu • Ngumi • Kujenga mwili","#00D4FF")
      ));
    }
    return groups.findAll();
  }
  private GymGroup mk(String n,String c,String d,String t,Integer p,Integer m,String e,String desc,String col){ GymGroup g=new GymGroup(); g.setName(n); g.setCoach(c); g.setDays(d); g.setTimeSlot(t); g.setPrice(p); g.setMembersCount(m); g.setEmoji(e); g.setDescription(desc); g.setColor(col); return g; }
  @PostMapping("/attendance") public Attendance attend(@RequestBody Map<String,String> b){ Attendance a=new Attendance(); a.setMemberName(b.get("memberName")); a.setGroupName(b.get("groupName")); return attends.save(a); }
  @GetMapping("/attendance") public List<Attendance> allAttend(){ return attends.findAllByOrderByCreatedAtDesc(); }
  @PostMapping("/join") public Map<String,Object> join(@RequestBody Map<String,String> b){ return Map.of("success",true,"message","Umejiunga na "+b.get("groupName")); }
}
