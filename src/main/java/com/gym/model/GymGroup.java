package com.gym.model;
import jakarta.persistence.*;
@Entity @Table(name="gym_groups")
public class GymGroup {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private String name; private String coach; private String days; private String timeSlot;
  private Integer price; private Integer membersCount=0; private String emoji; private String description; private String color;
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public String getName(){return name;} public void setName(String v){name=v;}
  public String getCoach(){return coach;} public void setCoach(String v){coach=v;}
  public String getDays(){return days;} public void setDays(String v){days=v;}
  public String getTimeSlot(){return timeSlot;} public void setTimeSlot(String v){timeSlot=v;}
  public Integer getPrice(){return price;} public void setPrice(Integer v){price=v;}
  public Integer getMembersCount(){return membersCount;} public void setMembersCount(Integer v){membersCount=v;}
  public String getEmoji(){return emoji;} public void setEmoji(String v){emoji=v;}
  public String getDescription(){return description;} public void setDescription(String v){description=v;}
  public String getColor(){return color;} public void setColor(String v){color=v;}
}
