package com.gym.model;
import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="payments")
public class Payment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long memberId; private Double amount; private LocalDate paymentDate=LocalDate.now(); private String method="CASH";
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getMemberId(){return memberId;} public void setMemberId(Long m){this.memberId=m;}
    public Double getAmount(){return amount;} public void setAmount(Double a){this.amount=a;}
    public LocalDate getPaymentDate(){return paymentDate;} public void setPaymentDate(LocalDate p){this.paymentDate=p;}
    public String getMethod(){return method;} public void setMethod(String m){this.method=m;}
}
