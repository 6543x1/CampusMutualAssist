package com.jessie.campusmutualassist.service;


import com.jessie.campusmutualassist.entity.User;
import com.jessie.campusmutualassist.entity.UserPortrait;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService
{

    void saveUser(User user);

    void savePhoneNumber(String username, String mobileNumber);
    boolean queryUser(String username);
    void saveImg(User user);

    List<Map<String,String>> getRealNameWithUsername(Set<String> usernameSet);

    User getUser(String username);

    User getNoPasswordUser(String username);

    void setNickName(User user);

    void setMailAddr(int uid, String mailAddr);

    String getMailAddr(String username);

    String getMailAddr(int uid);

    String getRealName(String username);

    void editPassword(String uid, String password);

    void setStatus(String username, int status);



    int getUid(String username);

    int calculateEvaluation(int status);

    void newUserPortrait(UserPortrait userPortrait);

    UserPortrait getUserPortrait(int uid);

    void updateAdditionalScore(int uid, int score);

    void updatePunishedScore(int uid, int score);

    int getAdditionalScore(int uid);

    String getImgPath(int uid);

    int newestUser();

    int getStatus(int uid);


}
