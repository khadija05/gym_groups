package com.gym.service;
import com.gym.model.User;
import com.gym.repository.UserRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service @EnableScheduling
public class AutoLockService {
  private final UserRepository users;
  public AutoLockService(UserRepository users){ this.users=users; }

  @Scheduled(cron = "0 0 6 * * *")
  @Transactional
  public void lockExpiredAdmins(){
    List<User> admins = users.findByRole("admin");
    LocalDateTime now = LocalDateTime.now();
    for(User admin : admins){
      if(admin.getSubscriptionExpiresAt()==null) continue;
      // Kama imeisha na bado ACTIVE
      if(admin.getSubscriptionExpiresAt().isBefore(now) && !"LOCKED".equalsIgnoreCase(admin.getStatus())){
        admin.setStatus("LOCKED");
        admin.setSubscriptionStatus("EXPIRED");
        admin.setActive(false);
        users.save(admin);
        System.out.println("🔒 AutoLock: " + admin.getUsername() + " amelockiwa");
      }
    }
  }

  @Transactional
  public int checkNow(){
    List<User> admins = users.findByRole("admin");
    LocalDateTime now = LocalDateTime.now();
    int locked=0;
    for(User admin : admins){
      if(admin.getSubscriptionExpiresAt()!=null && admin.getSubscriptionExpiresAt().isBefore(now) && !"LOCKED".equalsIgnoreCase(admin.getStatus())){
        admin.setStatus("LOCKED"); admin.setSubscriptionStatus("EXPIRED"); admin.setActive(false); users.save(admin); locked++;
      }
    }
    return locked;
  }

  @Transactional
  public void unlockAdmin(Long adminId, LocalDateTime newExpiry){
    User admin = users.findById(adminId).orElseThrow(() -> new RuntimeException("Admin hayupo"));
    admin.setStatus("ACTIVE"); admin.setSubscriptionStatus("ACTIVE"); admin.setActive(true);
    admin.setSubscriptionExpiresAt(newExpiry);
    users.save(admin);
  }
}
