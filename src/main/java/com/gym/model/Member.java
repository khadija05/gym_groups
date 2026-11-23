package com.gym.model;
import jakarta.persistence.*;
@Entity @Table(name="members")
public class Member {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private String fullName; private String phone; private String membershipType; private String status="ACTIVE";
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getFullName(){return fullName;} public void setFullName(String f){this.fullName=f;}
    public String getPhone(){return phone;} public void setPhone(String p){this.phone=p;}
    public String getMembershipType(){return membershipType;} public void setMembershipType(String m){this.membershipType=m;}
    public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
}
