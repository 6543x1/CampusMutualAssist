package com.jessie.campusmutualassist.service;

import java.util.Set;

public interface MailService {
    void sendResetPw(String dest, String theInfo);

    void sendNewMessage(String dest, String theInfo);

    void newMessage(String subject, String dest, String theInfo);

    void urgeStu(Set<String> urgeList, String urgeMessage);
}
