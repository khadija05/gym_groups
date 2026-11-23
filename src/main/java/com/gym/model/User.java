package com.gym.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="users")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private String name;
    @Column(unique=true) private String username;
    @Column(unique=true) private String phone;
    private String password;
    private String role="MEMBER"; // OWNER, ADMIN, MEMBER
    private String status="pending"; // pending, active, locked
    private String gymGroup;
    private String goal;
    private String plan="JARIBIO";
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt=LocalDateTime.now();
    private Long createdBy;
    // Admin subscription - analipa Owner kila mwezi
    private LocalDateTime subscriptionExpiresAt;
    private Double monthlyFee=50000.0;
    private String subscriptionStatus="active";
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String n){this.name=n;}
    public String getUsername(){return username;} public void setUsername(String u){this.username=u;}
    public String getPhone(){return phone;} public void setPhone(String p){this.phone=p;}
    public String getPassword(){return password;} public void setPassword(String p){this.password=p;}
    public String getRole(){return role;} public void setRole(String r){this.role=r;}
    public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
    public String getGymGroup(){return gymGroup;} public void setGymGroup(String g){this.gymGroup=g;}
    public String getGoal(){return goal;} public void setGoal(String g){this.goal=g;}
    public String getPlan(){return plan;} public void setPlan(String p){this.plan=p;}
    public LocalDateTime getExpiresAt(){return expiresAt;} public void setExpiresAt(LocalDateTime e){this.expiresAt=e;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime c){this.createdAt=c;}
    public Long getCreatedBy(){return createdBy;} public void setCreatedBy(Long c){this.createdBy=c;}
    public LocalDateTime getSubscriptionExpiresAt(){return subscriptionExpiresAt;} public void setSubscriptionExpiresAt(LocalDateTime d){this.subscriptionExpiresAt=d;}
    public Double getMonthlyFee(){return monthlyFee;} public void setMonthlyFee(Double f){this.monthlyFee=f;}
    public String getSubscriptionStatus(){return subscriptionStatus;} public void setSubscriptionStatus(String s){this.subscriptionStatus=s;}
}
