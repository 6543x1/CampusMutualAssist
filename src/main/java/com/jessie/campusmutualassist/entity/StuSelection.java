package com.jessie.campusmutualassist.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StuSelection implements Serializable {

  private String stuName;
  private String classID;
  private long scores;
}
