package com.jessie.campusmutualassist.entity;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AdminOperation implements Serializable
{

    private int operator;
    private String operation;
    private int targetUser;
    private int targetData;
    private LocalDateTime operatedTime;
    private String reason;

    public AdminOperation(int operator, String operation, int targetUser, int targetData, LocalDateTime operatedTime, String reason)
    {
        this.operator = operator;
        this.operation = operation;
        this.targetUser = targetUser;
        this.targetData = targetData;
        this.operatedTime = operatedTime;
        this.reason = reason;
    }

    public AdminOperation()
    {
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getOperatedTime()
    {
        return operatedTime;
    }

    public void setOperatedTime(LocalDateTime operatedTime)
    {
        this.operatedTime = operatedTime;
    }

    public int getOperator()
    {
        return operator;
    }

    public void setOperator(int operator)
    {
        this.operator = operator;
    }


    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }


    public int getTargetUser()
    {
        return targetUser;
    }

    public void setTargetUser(int targetUser)
    {
        this.targetUser = targetUser;
    }


    public int getTargetData()
    {
        return targetData;
    }

    public void setTargetData(int targetData)
    {
        this.targetData = targetData;
    }

}
