package com.gym.model;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="workouts")
public class Workout {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private Long memberId; private String planText; private LocalDateTime createdAt=LocalDateTime.now();
  public Long getId(){ return id; } public void setId(Long i){ this.id=i; }
  public Long getMemberId(){ return memberId; } public void setMemberId(Long m){ this.memberId=m; }
  public String getPlanText(){ return planText; } public void setPlanText(String p){ this.planText=p; }
  public LocalDateTime getCreatedAt(){ return createdAt; } public void setCreatedAt(LocalDateTime c){ this.createdAt=c; }
}
