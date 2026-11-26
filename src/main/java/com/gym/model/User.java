package com.gym.model;
import jakarta.persistence.*;
@Entity @Table(name="users")
public class User {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private Long gymId;
  @Column(nullable=false) private String fullName;
  @Column(unique=true, nullable=false) private String phone;
  @Column(nullable=false) private String passwordHash;
  @Column(nullable=false) private String role; // owner, admin, coach, member
  private boolean isActive = true;
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public Long getGymId(){return gymId;} public void setGymId(Long gymId){this.gymId=gymId;}
  public String getFullName(){return fullName;} public void setFullName(String n){this.fullName=n;}
  public String getPhone(){return phone;} public void setPhone(String p){this.phone=p;}
  public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String h){this.passwordHash=h;}
  public String getRole(){return role;} public void setRole(String r){this.role=r;}
  public boolean isActive(){return isActive;} public void setActive(boolean a){this.isActive=a;}
}
