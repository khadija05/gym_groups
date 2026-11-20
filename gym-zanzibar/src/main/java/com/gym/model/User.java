package com.gym.model;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="users")
public class User {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private String name; private String username; private String phone; private String password;
  private String role="MEMBER"; private String plan="JARIBIO"; private String status="pending";
  private Boolean approvedByKhadija=false; private Boolean locked=false;
  private LocalDateTime expiresAt; private String gymGroup; private String goal;
  private Double startWeight; private Double currentWeight; private Double goalWeight;
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public String getName(){return name;} public void setName(String v){name=v;}
  public String getUsername(){return username;} public void setUsername(String v){username=v;}
  public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
  public String getPassword(){return password;} public void setPassword(String v){password=v;}
  public String getRole(){return role;} public void setRole(String v){role=v;}
  public String getPlan(){return plan;} public void setPlan(String v){plan=v;}
  public String getStatus(){return status;} public void setStatus(String v){status=v;}
  public Boolean getApprovedByKhadija(){return approvedByKhadija;} public void setApprovedByKhadija(Boolean v){approvedByKhadija=v;}
  public Boolean getLocked(){return locked;} public void setLocked(Boolean v){locked=v;}
  public LocalDateTime getExpiresAt(){return expiresAt;} public void setExpiresAt(LocalDateTime v){expiresAt=v;}
  public String getGymGroup(){return gymGroup;} public void setGymGroup(String v){gymGroup=v;}
  public String getGoal(){return goal;} public void setGoal(String v){goal=v;}
  public Double getStartWeight(){return startWeight;} public void setStartWeight(Double v){startWeight=v;}
  public Double getCurrentWeight(){return currentWeight;} public void setCurrentWeight(Double v){currentWeight=v;}
  public Double getGoalWeight(){return goalWeight;} public void setGoalWeight(Double v){goalWeight=v;}
}
