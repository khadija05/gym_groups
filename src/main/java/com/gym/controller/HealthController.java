package com.gym.controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class HealthController {
  @GetMapping("/api/health")
  public Map<String,Object> health(){
    return Map.of("status","OK","message","Backend Iko Hai - GYM Zanzibar");
  }
  @GetMapping("/health")
  public Map<String,Object> health2(){
    return Map.of("status","OK");
  }
}
