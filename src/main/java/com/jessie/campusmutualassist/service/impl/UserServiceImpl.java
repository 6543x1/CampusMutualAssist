package com.jessie.campusmutualassist.service.impl;


import com.jessie.campusmutualassist.entity.User;
import com.jessie.campusmutualassist.mapper.UserMapper;
import com.jessie.campusmutualassist.service.UserService;
import com.jessie.campusmutualassist.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public User getNoPasswordUser(String username) {
        return userMapper.getNoPasswordUser(username);
    }

    @Override
    public void saveUser(User user) {
        userMapper.saveUser(user);
    }

    @Override
    public void savePhoneNumber(String username, String mobileNumber) {

    }

    @Override
    public int getUid(String username) {
        return userMapper.getUid(username);
    }


    @Override
    public List<Map<String, String>> getRealNameWithUsername(Set<String> usernameSet) {
        return userMapper.getRealNameWithUsername(usernameSet);
    }

    @Override
    public User getUser(String username) {
        return userMapper.getUser(username);
    }

    @Override
    public void setNickName(String username) {
        userMapper.setRealName(username);
    }

    @Override
    public void setMailAddr(int uid, String mailAddr) {
        userMapper.setMailAddr(uid, mailAddr);
    }

    @Override
    public String getMailAddr(String username) {
        return userMapper.getMailAddrByUsername(username);
    }

    @Override
    public String getMailAddr(int uid) {
        return userMapper.getMailAddrByUid(uid);
    }


    @Override
    public boolean queryUser(String username) {
        return userMapper.queryUser(username);
    }

    @Override
    public String getRealName(String username) {
        return userMapper.getNickNameByUsername(username);
    }

    @Override
    public void editPassword(String uid, String password) {
        userMapper.editPassword(uid, password);
    }

    @Override
    public void setStatus(String username, int status) {
        userMapper.setStatus(username, status);
    }


    @Override
    public void saveImg(User user) {
        userMapper.saveImg(user);
    }

    @Override
    public void updateAdditionalScore(int uid, int score) {
        //userPortraitDAO.updateAdditionalScore(uid, userPortraitDAO.getAdditionalScore(uid) + score);
    }

    @Override
    public int getAdditionalScore(int uid) {
        return 0;
    }

    @Override
    public void updatePunishedScore(int uid, int score) {
        // userPortraitDAO.updatePunishedScore(uid, userPortraitDAO.getPunishedScore(uid) + score);
    }

    @Override
    public String getImgPath(int uid) {
        return userMapper.getImgPathByUid(uid);
    }

    @Override
    public int getStatus(int uid) {
        return userMapper.getStatus(uid);
    }

    @Override
    public int calculateEvaluation(int status) {
        int evaluation = 0;
        if (status >= 105) {
            evaluation = 1;
        } else if (status >= 70) {
            evaluation = 2;
        } else if (status >= 40) {
            evaluation = 3;
        } else if (status < 40) {
            evaluation = 4;
        }
        return evaluation;//IDEA不给我弹优化了？？
    }

    @Override
    public boolean cmpPassword(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String realPassword = userMapper.getPassword(username);
        return bCryptPasswordEncoder.matches(password, realPassword);
    }
}
