package com.gym.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="users")
public class User {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private String fullName;
  private String phone;
  private String password;
  private String role = "MEMBER";
  private Long gymId;
  private String status = "ACTIVE";

  // HIZI NDIZO ZILIZOKOSEKANA KWA AutoLockService
  private String subscriptionStatus = "ACTIVE";
  private LocalDateTime subscriptionExpiresAt;
  private boolean active = true;

  public Long getId(){ return id; }
  public void setId(Long id){ this.id = id; }

  public String getFullName(){ return fullName; }
  public void setFullName(String n){ this.fullName = n; }

  public String getPhone(){ return phone; }
  public void setPhone(String p){ this.phone = p; }

  public String getPassword(){ return password; }
  public void setPassword(String p){ this.password = p; }

  public String getRole(){ return role; }
  public void setRole(String r){ this.role = r; }

  public Long getGymId(){ return gymId; }
  public void setGymId(Long g){ this.gymId = g; }

  public String getStatus(){ return status; }
  public void setStatus(String s){ this.status = s; }

  // --- MPYA KWA AutoLockService ---
  public String getSubscriptionStatus(){ return subscriptionStatus; }
  public void setSubscriptionStatus(String s){ this.subscriptionStatus = s; }

  public LocalDateTime getSubscriptionExpiresAt(){ return subscriptionExpiresAt; }
  public void setSubscriptionExpiresAt(LocalDateTime d){ this.subscriptionExpiresAt = d; }

  public boolean isActive(){ return active; }
  public boolean getActive(){ return active; }
  public void setActive(boolean a){ this.active = a; }
}
