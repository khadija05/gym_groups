
package com.gym.controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
public class HealthController {
  @GetMapping("/api/health")
  public Map<String,Object> health(){
    return Map.of("status","OK","backend","LIVE","time",System.currentTimeMillis());
  }
  @GetMapping("/api/auth/test")
  public Map<String,Object> test(){
    return Map.of("status","OK","auth","REACHABLE");
  }
}
