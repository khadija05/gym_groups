package com.gym.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  @GetMapping({"/", "/dashboard", "/home"})
  public String dashboard(){
    return "forward:/dashboard.html";
  }

  @GetMapping({"/owner", "/admin", "/member"})
  public String alias(){
    return "forward:/dashboard.html";
  }
}
