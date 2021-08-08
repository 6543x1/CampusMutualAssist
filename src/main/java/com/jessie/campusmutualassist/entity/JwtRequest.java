package com.jessie.campusmutualassist.entity;

import java.io.Serializable;

public class JwtRequest implements Serializable
{

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;

    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }

    @Override
    public String toString()
    {
        return "JwtRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
