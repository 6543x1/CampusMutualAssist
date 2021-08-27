package com.jessie.campusmutualassist.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StuSelection implements Serializable {
  //实际上兼具记录学生选择＆分数情况作用
  private String stuName;
  private String name;
  private String classID;
  private long scores;
}
