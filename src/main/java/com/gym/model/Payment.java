package com.gym.model;
import jakarta.persistence.*; import java.time.LocalDateTime;

@Entity @Table(name="payments")
public class Payment {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private Long gymId; private Long memberId; private String phone;
  private Integer amount; private String plan; private String reference; private String status="PENDING";
  private LocalDateTime createdAt=LocalDateTime.now();

  // ===== MPYA - ILI OWNER AONE GYM NA ADMIN BILA KUHANGAIKA =====
  private String gymName;
  private String adminName;
  private String adminPhone;

  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public Long getGymId(){return gymId;} public void setGymId(Long g){this.gymId=g;}
  public Long getMemberId(){return memberId;} public void setMemberId(Long m){this.memberId=m;}
  public String getPhone(){return phone;} public void setPhone(String p){this.phone=p;}
  public Integer getAmount(){return amount;} public void setAmount(Integer a){this.amount=a;}
  public String getPlan(){return plan;} public void setPlan(String p){this.plan=p;}
  public String getReference(){return reference;} public void setReference(String r){this.reference=r;}
  public String getStatus(){return status;} public void setStatus(String s){this.status=s;}

  // HII ILIKUWA INAKOSEKANA - SASA IPO
  public LocalDateTime getCreatedAt(){ return createdAt; }
  public void setCreatedAt(LocalDateTime c){ this.createdAt = c; }

  // ===== MPYA =====
  public String getGymName(){ return gymName; }
  public void setGymName(String n){ this.gymName = n; }

  public String getAdminName(){ return adminName; }
  public void setAdminName(String n){ this.adminName = n; }

  public String getAdminPhone(){ return adminPhone; }
  public void setAdminPhone(String p){ this.adminPhone = p; }
}
