package com.gym.model;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="attendance")
public class Attendance {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private String memberName; private String groupName; private LocalDateTime createdAt=LocalDateTime.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public String getMemberName(){return memberName;} public void setMemberName(String v){memberName=v;}
  public String getGroupName(){return groupName;} public void setGroupName(String v){groupName=v;}
  public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}
