package com.gym.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity @Table(name="gyms")
public class Gym {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private String name; private String coach; private String days; private String timeSlot;
  private Integer price; private Integer membersCount = 0;
  private String emoji; private String description; private String color; private String location;
  private boolean isActive = true;

  // ===== MPYA KWA OWNER - MALIPO + SIKU ZILIZOBAKI =====
  private String status = "ACTIVE"; // ACTIVE, PENDING, EXPIRED
  private LocalDate packageEndDate; // Tarehe package inaisha
  private String adminPhone; // Simu ya Admin wa Gym hii - kwa kumtumia message
  private String adminName; // Jina la Admin

  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public String getName(){return name;} public void setName(String n){this.name=n;}
  public String getCoach(){return coach;} public void setCoach(String c){this.coach=c;}
  public String getDays(){return days;} public void setDays(String d){this.days=d;}
  public String getTimeSlot(){return timeSlot;} public void setTimeSlot(String t){this.timeSlot=t;}
  public Integer getPrice(){return price;} public void setPrice(Integer p){this.price=p;}
  public Integer getMembersCount(){return membersCount;} public void setMembersCount(Integer m){this.membersCount=m;}
  public String getEmoji(){return emoji;} public void setEmoji(String e){this.emoji=e;}
  public String getDescription(){return description;} public void setDescription(String d){this.description=d;}
  public String getColor(){return color;} public void setColor(String c){this.color=c;}
  public String getLocation(){return location;} public void setLocation(String l){this.location=l;}
  public boolean isActive(){return isActive;} public void setActive(boolean a){this.isActive=a;}

  // ===== GETTERS / SETTERS MPYA =====
  public String getStatus(){ return status; }
  public void setStatus(String s){ this.status = s; }

  public LocalDate getPackageEndDate(){ return packageEndDate; }
  public void setPackageEndDate(LocalDate d){ this.packageEndDate = d; }

  public String getAdminPhone(){ return adminPhone; }
  public void setAdminPhone(String p){ this.adminPhone = p; }

  public String getAdminName(){ return adminName; }
  public void setAdminName(String n){ this.adminName = n; }

  // ===== SIKU ZILIZOBAKI - INAHESABIKA KIOTOMATIKI =====
  // Kama packageEndDate ni null, inarudisha 30 ili Gym za zamani zisionekane zimeisha
  public long getDaysLeft(){
    if(packageEndDate == null) return 30;
    return ChronoUnit.DAYS.between(LocalDate.now(), packageEndDate);
  }

  // Kama Gym imeisha muda, status inakuwa EXPIRED kiotomatiki
  public String getWarning(){
    long days = getDaysLeft();
    if(days <= 0) return "IMESHAISHA ❌";
    if(days <= 5) return "INAKARIBIA KUISHIA ⚠️";
    return "SAWA ✅";
  }
}
