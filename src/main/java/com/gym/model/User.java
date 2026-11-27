package com.gym.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity @Table(name="users")
public class User {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private Long gymId;
  @Column(nullable=false) private String fullName;
  private String name;
  private String username;
  @Column(unique=true, nullable=false) private String phone;
  @Column(nullable=false) private String passwordHash;
  @Column(nullable=false) private String role;
  private boolean isActive = true;
  private String status = "ACTIVE";
  private String subscriptionStatus = "ACTIVE";
  private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime expiresAt;
  private LocalDateTime subscriptionExpiresAt;
  private LocalDate subscriptionExpiresDate;
  private LocalDate trialEndsAt;

  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public Long getGymId(){return gymId;} public void setGymId(Long g){this.gymId=g;}
  public String getFullName(){ return fullName != null ? fullName : name; }
  public void setFullName(String n){ this.fullName=n; this.name=n; }
  public String getName(){ return fullName != null ? fullName : name; }
  public void setName(String n){ this.name=n; this.fullName=n; }
  public String getUsername(){ if(username!=null && !username.isEmpty()) return username; return phone; }
  public void setUsername(String u){this.username=u;}
  public String getPhone(){return phone;} public void setPhone(String p){this.phone=p; if(this.username==null) this.username=p;}
  public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String h){this.passwordHash=h;}
  public String getRole(){return role;} public void setRole(String r){this.role=r;}
  public boolean isActive(){return isActive;} public void setActive(boolean a){this.isActive=a;}
  public String getStatus(){return status;}
  public void setStatus(String s){ this.status=s; this.isActive = !"LOCKED".equalsIgnoreCase(s) && !"EXPIRED".equalsIgnoreCase(s); }
  public String getSubscriptionStatus(){return subscriptionStatus;}
  public void setSubscriptionStatus(String s){this.subscriptionStatus=s;}
  public LocalDateTime getCreatedAt(){return createdAt;}
  public void setCreatedAt(LocalDateTime c){this.createdAt=c;}
  public LocalDateTime getExpiresAt(){return expiresAt;}
  public void setExpiresAt(LocalDateTime e){this.expiresAt=e; this.subscriptionExpiresAt=e;}
  public LocalDateTime getSubscriptionExpiresAt(){
    if(subscriptionExpiresAt!=null) return subscriptionExpiresAt;
    if(expiresAt!=null) return expiresAt;
    if(subscriptionExpiresDate!=null) return subscriptionExpiresDate.atStartOfDay();
    return null;
  }
  public void setSubscriptionExpiresAt(LocalDateTime d){ this.subscriptionExpiresAt=d; this.expiresAt=d; }
  public void setSubscriptionExpiresAt(LocalDate d){
    this.subscriptionExpiresDate=d;
    if(d!=null){ this.subscriptionExpiresAt=d.atStartOfDay(); this.expiresAt=d.atStartOfDay(); }
  }
  public LocalDate getTrialEndsAt(){return trialEndsAt;}
  public void setTrialEndsAt(LocalDate d){this.trialEndsAt=d;}
  @PrePersist public void onCreate(){
    if(createdAt==null) createdAt=LocalDateTime.now();
    if(username==null) username=phone;
    if(status==null) status="ACTIVE";
    if(subscriptionStatus==null) subscriptionStatus="ACTIVE";
    if(name==null) name=fullName;
    if(fullName==null) fullName=name;
  }
}
