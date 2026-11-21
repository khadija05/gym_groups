package com.gym.controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
public class HomeController {
  @GetMapping("/")
  public Map<String,Object> home() {
    Map<String,Object> res = new HashMap<>();
    res.put("success", true);
    res.put("message", "Gym Groups API is running - Karibu!");
    return res;
  }
}
