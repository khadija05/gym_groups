package com.gym.model;
import jakarta.persistence.*;
@Entity @Table(name="workout_plans")
public class WorkoutPlan {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private Long gymId; private Long memberId; private Long coachId;
  @Column(columnDefinition="TEXT") private String planText;
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public Long getGymId(){return gymId;} public void setGymId(Long g){this.gymId=g;}
  public Long getMemberId(){return memberId;} public void setMemberId(Long m){this.memberId=m;}
  public Long getCoachId(){return coachId;} public void setCoachId(Long c){this.coachId=c;}
  public String getPlanText(){return planText;} public void setPlanText(String p){this.planText=p;}
}
