package com.gym.model;
import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="members")
public class Member {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private Long userId; private Long gymId; private String plan="Mwezi 1";
  private LocalDate startDate=LocalDate.now(); private LocalDate endDate=LocalDate.now().plusDays(30);
  private Double weightStart; private Double weightNow; private Double weightGoal;
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public Long getUserId(){return userId;} public void setUserId(Long u){this.userId=u;}
  public Long getGymId(){return gymId;} public void setGymId(Long g){this.gymId=g;}
  public String getPlan(){return plan;} public void setPlan(String p){this.plan=p;}
  public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate d){this.startDate=d;}
  public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate d){this.endDate=d;}
  public Double getWeightStart(){return weightStart;} public void setWeightStart(Double w){this.weightStart=w;}
  public Double getWeightNow(){return weightNow;} public void setWeightNow(Double w){this.weightNow=w;}
  public Double getWeightGoal(){return weightGoal;} public void setWeightGoal(Double w){this.weightGoal=w;}
}
