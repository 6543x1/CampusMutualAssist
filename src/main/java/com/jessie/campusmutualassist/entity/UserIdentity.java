package com.jessie.campusmutualassist.entity;


import java.io.Serializable;

public class UserIdentity implements Serializable
{

    private int uid;
    private String No;
    private String school;


    public int getUid()
    {
        return uid;
  }

  public void setUid(int uid)
  {
    this.uid = uid;
  }


  public String getNo()
  {
      return No;
  }

  public void setNo(String no)
  {
      this.No = no;
  }


  public String getSchool()
  {
    return school;
  }

  public void setSchool(String school)
  {
    this.school = school;
  }

}
