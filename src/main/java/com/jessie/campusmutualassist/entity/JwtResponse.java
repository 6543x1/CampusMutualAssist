package com.jessie.campusmutualassist.entity;

import java.io.Serializable;

public class JwtResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String jwtToken;
    private User user;

    public JwtResponse(String jwtToken, User user) {
        this.jwtToken = jwtToken;
        user.setPassword("*不告诉你*");
        this.user = user;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "jwtToken='" + jwtToken + '\'' +
                ", user=" + user +
                '}';
    }
}
