package com.jessie.campusmutualassist.entity;


import java.io.Serializable;

public class TeachingClass implements Serializable {

  private String name;
  private String schedule;
  private String teacher;
  private String college;
  private String id;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public String getSchedule() {
    return schedule;
  }

  public void setSchedule(String schedule) {
    this.schedule = schedule;
  }


  public String getTeacher() {
    return teacher;
  }

  public void setTeacher(String teacher) {
    this.teacher = teacher;
  }


  public String getCollege() {
    return college;
  }

  public void setCollege(String college) {
    this.college = college;
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

}
