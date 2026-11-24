package com.gym.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="payments")
public class Payment {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
private Long userId; private Long memberId; private String memberName; private Double amount=0.0;
private String method="Cash"; private String status="paid"; private String date; private LocalDateTime createdAt=LocalDateTime.now();
public Long getId(){return id;} public void setId(Long id){this.id=id;}
public Long getUserId(){return userId;} public void setUserId(Long u){this.userId=u;}
public Long getMemberId(){return memberId;} public void setMemberId(Long m){this.memberId=m;}
public String getMemberName(){return memberName;} public void setMemberName(String n){this.memberName=n;}
public Double getAmount(){return amount;} public void setAmount(Double a){this.amount=a;}
public String getMethod(){return method;} public void setMethod(String m){this.method=m;}
public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
public String getDate(){return date;} public void setDate(String d){this.date=d;}
public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime c){this.createdAt=c;}
}
