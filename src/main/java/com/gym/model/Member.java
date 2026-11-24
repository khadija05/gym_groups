package com.gym.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="members")
public class Member {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
private String name; private String phone; private String gymGroup; private String status="pending";
private String goal; private String plan="JARIBIO"; private Double monthlyFee=50000.0;
private LocalDateTime expiresAt; private LocalDateTime createdAt=LocalDateTime.now(); private Long createdBy;
public Long getId(){return id;} public void setId(Long id){this.id=id;}
public String getName(){return name;} public void setName(String n){this.name=n;}
public String getPhone(){return phone;} public void setPhone(String p){this.phone=p;}
public String getGymGroup(){return gymGroup;} public void setGymGroup(String g){this.gymGroup=g;}
public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
public String getGoal(){return goal;} public void setGoal(String g){this.goal=g;}
public String getPlan(){return plan;} public void setPlan(String p){this.plan=p;}
public Double getMonthlyFee(){return monthlyFee;} public void setMonthlyFee(Double f){this.monthlyFee=f;}
public LocalDateTime getExpiresAt(){return expiresAt;} public void setExpiresAt(LocalDateTime e){this.expiresAt=e;}
public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime c){this.createdAt=c;}
public Long getCreatedBy(){return createdBy;} public void setCreatedBy(Long c){this.createdBy=c;}
}
