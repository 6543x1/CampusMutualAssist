package com.jessie.campusmutualassist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jessie.campusmutualassist.entity.SignIn;

import java.util.List;

/**
 *
 */
public interface SignInService extends IService<SignIn> {

    void newSignIn(SignIn signIn);

    SignIn getSignIn(long signInID);

    List<SignIn> getStuSignIn(String classID);

    List getMyNotSign(String classID, String username);

    void deleteSignIn(String classID, long signID);
}
