package com.gym.model;
import jakarta.persistence.*; import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity @Table(name="members")
public class Member {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private Long userId; private Long gymId; private String plan="Mwezi 1";
  private LocalDate startDate=LocalDate.now(); private LocalDate endDate=LocalDate.now().plusDays(30);
  private Double weightStart; private Double weightNow; private Double weightGoal;

  // ===== MPYA KWA DASHBOARD - MEMBER + SIKU ZILIZOBAKI =====
  private String fullName; // Jina la Member - ili Owner / Admin amuone bila kuunganisha User
  private String phone; // Simu ya Member - kwa kumtumia message
  private String password; // Password ya Member kuingia
  private String status = "ACTIVE"; // ACTIVE, EXPIRED, PENDING
  private String role = "MEMBER";

  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public Long getUserId(){return userId;} public void setUserId(Long u){this.userId=u;}
  public Long getGymId(){return gymId;} public void setGymId(Long g){this.gymId=g;}
  public String getPlan(){return plan;} public void setPlan(String p){this.plan=p;}
  public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate d){this.startDate=d;}
  public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate d){this.endDate=d;}
  public Double getWeightStart(){return weightStart;} public void setWeightStart(Double w){this.weightStart=w;}
  public Double getWeightNow(){return weightNow;} public void setWeightNow(Double w){this.weightNow=w;}
  public Double getWeightGoal(){return weightGoal;} public void setWeightGoal(Double w){this.weightGoal=w;}

  // ===== GETTERS / SETTERS MPYA =====
  public String getFullName(){ return fullName; }
  public void setFullName(String n){ this.fullName = n; }
  // Alias kwa dashboard ya zamani
  public String getFull_name(){ return fullName; }

  public String getPhone(){ return phone; }
  public void setPhone(String p){ this.phone = p; }

  public String getPassword(){ return password; }
  public void setPassword(String p){ this.password = p; }

  public String getStatus(){ return status; }
  public void setStatus(String s){ this.status = s; }

  public String getRole(){ return role; }
  public void setRole(String r){ this.role = r; }

  // ===== SIKU ZILIZOBAKI ZA MEMBER KULIPIA PACKAGE =====
  // Hii ndiyo unaitaka - Member anaona siku zilobaki, Admin / Owner wanaona pia
  public long getDaysLeft(){
    if(endDate == null) return 0;
    return ChronoUnit.DAYS.between(LocalDate.now(), endDate);
  }

  public String getWarning(){
    long days = getDaysLeft();
    if(days <= 0) return "IMESHAISHA ❌";
    if(days <= 5) return "INAKARIBIA KUISHIA ⚠️";
    return "SAWA ✅";
  }

  // Kama muda umeisha, status inakuwa EXPIRED kiotomatiki kwenye kuonyesha
  public String getDisplayStatus(){
    if(getDaysLeft() <= 0) return "Imeisha";
    return status == null ? "Active" : status;
  }
}
