package com.gym.controller;
import com.gym.model.*; import com.gym.repository.*; import org.springframework.web.bind.annotation.*; import java.util.List; import java.util.Map;
@RestController @CrossOrigin(origins="*") @RequestMapping("/api")
public class GymController {
    private final MemberRepository members; private final PaymentRepository payments; private final AttendanceRepository attendance;
    public GymController(MemberRepository m, PaymentRepository p, AttendanceRepository a){ this.members=m; this.payments=p; this.attendance=a; }
    @GetMapping("/members") public List<Member> allMembers(){ return members.findAll(); }
    @PostMapping("/members") public Member addMember(@RequestBody Member m){ return members.save(m); }
    @GetMapping("/payments") public List<Payment> allPayments(){ return payments.findAll(); }
    @PostMapping("/payments") public Payment addPayment(@RequestBody Payment p){ return payments.save(p); }
    @GetMapping("/gym/attendance") public List<Attendance> allAttendance(){ return attendance.findAll(); }
    @PostMapping("/gym/attendance") public Attendance checkIn(@RequestBody Attendance a){ return attendance.save(a); }
    @GetMapping("/health") public String health(){ return "Gym Zanzibar API is running"; }
    @GetMapping("/gym/groups") public List<Map<String,Object>> groups(){
        return List.of(
            Map.of("name","Gym Stone Town - Jenga Mwili","coach","Kocha Juma","days","Jumatatu - Jumamosi","timeSlot","5:00 Asubuhi - 9:00 Jioni","price",30000,"membersCount",25,"emoji","🏋️","description","Kujenga misuli","color","#FFD400"),
            Map.of("name","Gym Mjini - Punguza Uzito","coach","Kocha Amina","days","Jumatatu, Jumatano, Ijumaa","timeSlot","6:00 Asubuhi • 6:00 Jioni","price",30000,"membersCount",18,"emoji","🔥","description","Punguza uzito","color","#FF6B00"),
            Map.of("name","Gym Forodhani - Wanawake Tu","coach","Kocha Fatma","days","Jumanne, Alhamisi, Jumamosi","timeSlot","6:00 Jioni - 8:00 Jioni","price",25000,"membersCount",12,"emoji","🧘","description","Wanawake tu","color","#FF69B4"),
            Map.of("name","Gym Mjini Magharibi - Wanaume Tu","coach","Kocha Ali","days","Kila Siku","timeSlot","5:00 Asubuhi - 10:00 Jioni","price",30000,"membersCount",22,"emoji","💪","description","Wanaume tu","color","#00D4FF")
        );
    }
}
