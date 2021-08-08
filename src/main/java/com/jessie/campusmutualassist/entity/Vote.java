package com.jessie.campusmutualassist.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote implements Serializable {
    long vid;
    String classID;
    String title;
    int limitation;
    Set<String> Selections;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedTime;
    boolean anonymous;
    //友情提示：现在不加这个format注解时间会变成乱码
}
