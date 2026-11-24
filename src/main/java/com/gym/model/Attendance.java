package com.gym.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="attendance")
public class Attendance {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
private Long memberId; private String memberName; private String date; private String status="present"; private LocalDateTime createdAt=LocalDateTime.now();
public Long getId(){return id;} public void setId(Long id){this.id=id;}
public Long getMemberId(){return memberId;} public void setMemberId(Long m){this.memberId=m;}
public String getMemberName(){return memberName;} public void setMemberName(String n){this.memberName=n;}
public String getDate(){return date;} public void setDate(String d){this.date=d;}
public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime c){this.createdAt=c;}
}
