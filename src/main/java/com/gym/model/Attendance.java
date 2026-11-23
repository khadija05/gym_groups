package com.gym.model;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="attendance")
public class Attendance {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long memberId; private String memberName; private String groupName; private LocalDateTime checkIn=LocalDateTime.now();
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getMemberId(){return memberId;} public void setMemberId(Long m){this.memberId=m;}
    public String getMemberName(){return memberName;} public void setMemberName(String n){this.memberName=n;}
    public String getGroupName(){return groupName;} public void setGroupName(String g){this.groupName=g;}
    public LocalDateTime getCheckIn(){return checkIn;} public void setCheckIn(LocalDateTime c){this.checkIn=c;}
}
