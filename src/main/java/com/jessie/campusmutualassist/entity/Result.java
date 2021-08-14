package com.jessie.campusmutualassist.entity;


import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable
{
    int code;
    boolean status;
    String msg;
    T data;

    /*
    * {
    "timestamp": 1617713474510,
    "status": 403,
    "error": "Forbidden",
    "message": "",
    "path": "/user/isLogin"
}*/
    @Override
    public String toString()
    {
        return "Result{" +
                "code=" + code +
                ", status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static <T> Result<T> success(String msg)
    {
        return new Result<T>(msg, 200);

    }

    public static <T> Result<T> success(String msg, T data)
    {
        return new Result<T>(msg, data, true);
    }

    public static <T> Result<T> error(String msg)
    {
        return new Result<T>(msg);
    }
    public static <T> Result<T> error(String msg,int code,T data)
    {
        return new Result<T>(msg,code,data);
    }


    public static <T> Result<T> error(String msg, int code)
    {
        return new Result<T>(msg, code);
    }

    public static <T> Result<T> msg(String msg, T data, boolean status)
    {
        return new Result<T>(msg, data, status);
    }

    private Result(T data)
    {
        this.code = 200;
        this.msg = "success";
        this.status = true;
        this.data = data;

    }

    private Result(String msg)
    {
        if (msg == null) {
            return;
        }
        this.code = 400;
        this.status = false;
        this.msg = msg;
    }

    private Result(String msg, int code)
    {
        if (msg == null) {
            return;
        }
        this.msg = msg;
        this.code = code;
        status = code == 200;
    }
    private Result(String msg, int code,T data)
    {
        if (msg == null) {
            return;
        }
        this.msg = msg;
        this.code = code;
        status = code == 200;
        this.data=data;
    }

    private Result(String msg, T data, boolean status)
    {
        this.code = status ? 200 : 400;
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

}
