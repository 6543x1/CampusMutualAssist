package com.jessie.campusmutualassist.entity;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminOperation implements Serializable
{

    private String operator;
    private String operation;
    private String targetUser;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operatedTime;
    private String reason;

}
