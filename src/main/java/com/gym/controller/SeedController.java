
package com.gym.controller;
import com.gym.model.*;
import com.gym.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
public class SeedController {
  private final UserRepo users;
  private final PasswordEncoder encoder;
  public SeedController(UserRepo u, PasswordEncoder e){ this.users=u; this.encoder=e; }

  @GetMapping("/seed-owner")
  public Map<String,Object> seed(){
    String phone = "0770000000";
    var existing = users.findByPhone(phone);
    if(existing.isPresent()) return Map.of("message","Owner tayari yupo","phone",phone,"password","123456");
    User u = new User();
    u.setFullName("Owner Zanzibar");
    u.setPhone(phone);
    u.setPasswordHash(encoder.encode("123456"));
    u.setRole("OWNER");
    u.setActive(true);
    users.save(u);
    return Map.of("message","Owner ametengenezwa","phone",phone,"password","123456");
  }
}
