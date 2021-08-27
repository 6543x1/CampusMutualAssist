package com.jessie.campusmutualassist.entity;


import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@ApiModel("公告类")
public class Notice implements Serializable {
  @ApiModelProperty(value = "公告的唯一ID，服务器自动生成",required = false)
  private long nid;
  private String title;
  private String body;
  private String classID;
  @ApiModelProperty(value = "服务器自动生成")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime publishedTime;
  @ApiModelProperty(value = "服务器自动生成")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime deadLine;
  @ApiModelProperty(value = "服务器自动生成")
  private String publisher;
  @ApiModelProperty("是否需要确认")
  private boolean confirm;
  @ApiModelProperty("分类")
  private String type;
  @ApiModelProperty("是否所有人可见")
  private boolean isPublic=true;




}
