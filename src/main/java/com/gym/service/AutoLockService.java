package com.gym.service;
import com.gym.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class AutoLockService {
  private final GymRepo gyms;
  public AutoLockService(GymRepo g){ this.gyms = g; }

  // Kila siku saa 12 asubuhi: Gym zilizoisha siku, ziwe EXPIRED
  @Scheduled(cron = "0 0 0 * * *")
  public void lockExpiredGyms(){
    for(var g : gyms.findAll()){
      if(g.getPackageEndDate() != null && g.getPackageEndDate().isBefore(LocalDate.now())){
        try{ g.setStatus("EXPIRED"); }catch(Exception ignored){}
        gyms.save(g);
      }
    }
  }
}
