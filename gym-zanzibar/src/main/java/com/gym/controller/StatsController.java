
package com.gym.controller;
import com.gym.repository.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api") @CrossOrigin("*")
public class StatsController {
  private final UserRepository users; private final AttendanceRepository attends;
  public StatsController(UserRepository u, AttendanceRepository a){ this.users=u; this.attends=a; }
  @GetMapping("/stats") public Map<String,Object> stats(){
    return Map.of("members", users.count(), "groups", 4, "coaches", 4, "today", attends.count(), "status","OK");
  }
  @GetMapping("/health") public Map<String,Object> health(){ return Map.of("status","OK","message","Backend iko hai"); }
  @GetMapping("/") public Map<String,Object> root(){ return Map.of("status","OK","message","Gym Zanzibar Backend iko hai"); }
}
