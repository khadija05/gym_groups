package com.gym.model;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="admin_payments")
public class AdminPayment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long adminId; private String adminName; private String groupName;
    private Double amount; private String month; private String reference;
    private String status="pending"; private LocalDateTime createdAt=LocalDateTime.now(); private LocalDateTime approvedAt;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getAdminId(){return adminId;} public void setAdminId(Long a){this.adminId=a;}
    public String getAdminName(){return adminName;} public void setAdminName(String n){this.adminName=n;}
    public String getGroupName(){return groupName;} public void setGroupName(String g){this.groupName=g;}
    public Double getAmount(){return amount;} public void setAmount(Double a){this.amount=a;}
    public String getMonth(){return month;} public void setMonth(String m){this.month=m;}
    public String getReference(){return reference;} public void setReference(String r){this.reference=r;}
    public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime c){this.createdAt=c;}
    public LocalDateTime getApprovedAt(){return approvedAt;} public void setApprovedAt(LocalDateTime a){this.approvedAt=a;}
}
