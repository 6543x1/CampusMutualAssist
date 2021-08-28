package com.jessie.campusmutualassist.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StuSelection implements Serializable {
  //实际上兼具记录学生选择＆分数情况作用
  @ApiModelProperty(value = "学生用户名",notes = "这里有个失误，一开始叫stuName，实际上应该是StuUsername，懒得改了...")
  private String stuName;//这里有个失误，一开始叫stuName，实际上应该是username，表就懒得改了..........
  private String name;
  private String classID;
  private long scores;
}
