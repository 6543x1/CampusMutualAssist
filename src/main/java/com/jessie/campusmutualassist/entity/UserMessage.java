package com.jessie.campusmutualassist.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage implements Serializable {
    private String data;
    private String source;
    private LocalDateTime time;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getTime() {
        return time;
    }
}
