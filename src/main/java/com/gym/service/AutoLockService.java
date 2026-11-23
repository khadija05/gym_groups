package com.gym.service;
import com.gym.model.User; import com.gym.repository.UserRepository;
import org.springframework.scheduling.annotation.EnableScheduling; import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service; import java.time.LocalDateTime; import java.util.List;
@Service @EnableScheduling
public class AutoLockService {
    private final UserRepository users;
    public AutoLockService(UserRepository u){this.users=u;}
    @Scheduled(fixedRate=60000)
    public void autoLockExpiredAdmins(){
        List<User> admins = users.findAll().stream().filter(u->"ADMIN".equals(u.getRole())).toList();
        for(User admin: admins){
            if(admin.getSubscriptionExpiresAt()==null){
                admin.setSubscriptionExpiresAt(admin.getCreatedAt().plusDays(30)); users.save(admin); continue;
            }
            if(admin.getSubscriptionExpiresAt().isBefore(LocalDateTime.now())){
                if(!"locked".equals(admin.getStatus())){
                    admin.setStatus("locked"); admin.setSubscriptionStatus("locked"); users.save(admin);
                    System.out.println("🔒 Admin "+admin.getUsername()+" amejifunga - hajalipa");
                    users.findAll().stream().filter(m->"MEMBER".equals(m.getRole()) && admin.getId().equals(m.getCreatedBy())).forEach(m->{ m.setStatus("locked"); users.save(m); });
                }
            }
        }
    }
}
