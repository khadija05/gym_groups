package com.gym.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="admin_payments")
public class AdminPayment {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
private Long adminId; private String adminName; private Double amount; private String month; private String reference; private String method; private String status="pending"; private LocalDateTime createdAt=LocalDateTime.now();
public Long getId(){return id;} public void setId(Long id){this.id=id;}
public Long getAdminId(){return adminId;} public void setAdminId(Long v){this.adminId=v;}
public String getAdminName(){return adminName;} public void setAdminName(String v){this.adminName=v;}
public Double getAmount(){return amount;} public void setAmount(Double v){this.amount=v;}
public String getMonth(){return month;} public void setMonth(String v){this.month=v;}
public String getReference(){return reference;} public void setReference(String v){this.reference=v;}
public String getMethod(){return method;} public void setMethod(String v){this.method=v;}
public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){this.createdAt=v;}
}
