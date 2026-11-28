package com.gym.model;
import jakarta.persistence.*;

@Entity @Table(name="users")
public class User {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private String fullName;
  private String phone;
  private String password; // HII NDIO ILIYOKOSEKANA
  private String role = "MEMBER"; // OWNER, ADMIN, COACH, MEMBER
  private Long gymId;
  private String status = "ACTIVE";

  public Long getId(){ return id; }
  public void setId(Long id){ this.id = id; }

  public String getFullName(){ return fullName; }
  public void setFullName(String n){ this.fullName = n; }

  public String getPhone(){ return phone; }
  public void setPhone(String p){ this.phone = p; }

  // HIZI MBILI NDIZO ZILIZOKOSEKANA - SASA ZIPO
  public String getPassword(){ return password; }
  public void setPassword(String p){ this.password = p; }

  public String getRole(){ return role; }
  public void setRole(String r){ this.role = r; }

  public Long getGymId(){ return gymId; }
  public void setGymId(Long g){ this.gymId = g; }
  // Alias kwa code ya zamani
  public Long getGym_id(){ return gymId; }

  public String getStatus(){ return status; }
  public void setStatus(String s){ this.status = s; }
}
