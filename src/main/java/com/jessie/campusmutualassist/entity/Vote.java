package com.jessie.campusmutualassist.entity;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote implements Serializable {
    long vid;
    String classID;
    @ApiModelProperty(required = true)
    String title;
    @ApiModelProperty(value = "一次可选的选项个数")
    int limitation;
    @ApiModelProperty(value = "选项，可以有多个，注意是Set类型，不能有重复的选项")
    Set<String> Selections;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedTime;
    @ApiModelProperty(value = "是否匿名")
    boolean anonymous;
    String publisher;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime deadLine;
    //友情提示：现在不加这个format注解时间会变成乱码
    boolean voted;
}
