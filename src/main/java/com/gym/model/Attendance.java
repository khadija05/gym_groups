package com.gym.model;
import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="attendance")
public class Attendance {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private Long gymId; private Long memberId; private String memberName; private String groupName;
  private LocalDate date=LocalDate.now(); private String timeSlot;
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public Long getGymId(){return gymId;} public void setGymId(Long g){this.gymId=g;}
  public Long getMemberId(){return memberId;} public void setMemberId(Long m){this.memberId=m;}
  public String getMemberName(){return memberName;} public void setMemberName(String n){this.memberName=n;}
  public String getGroupName(){return groupName;} public void setGroupName(String g){this.groupName=g;}
  public LocalDate getDate(){return date;} public void setDate(LocalDate d){this.date=d;}
  public String getTimeSlot(){return timeSlot;} public void setTimeSlot(String t){this.timeSlot=t;}
}
