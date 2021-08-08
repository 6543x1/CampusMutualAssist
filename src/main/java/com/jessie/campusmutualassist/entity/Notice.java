package com.jessie.campusmutualassist.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@ApiModel("公告类")
public class Notice implements Serializable {
  @ApiModelProperty("公告的唯一ID，因为不好设置主键就搞了这个")
  private long nid;
  private String title;
  private String body;
  private String classID;
  private LocalDateTime publishedTime;
  private String publisher;
  @ApiModelProperty("是否需要确认")
  private boolean confirm;




}
