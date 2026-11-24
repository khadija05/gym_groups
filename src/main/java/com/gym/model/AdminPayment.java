package com.gym.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="admin_payments")
public class AdminPayment {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
private Long adminId; private String adminName; private Double amount=0.0; private String month; private String status="paid"; private LocalDateTime createdAt=LocalDateTime.now();
public Long getId(){return id;} public void setId(Long id){this.id=id;}
public Long getAdminId(){return adminId;} public void setAdminId(Long a){this.adminId=a;}
public String getAdminName(){return adminName;} public void setAdminName(String n){this.adminName=n;}
public Double getAmount(){return amount;} public void setAmount(Double a){this.amount=a;}
public String getMonth(){return month;} public void setMonth(String m){this.month=m;}
public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime c){this.createdAt=c;}
}
