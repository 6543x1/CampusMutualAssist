package com.jessie.campusmutualassist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jessie.campusmutualassist.entity.SignIn;

/**
 *
 */
public interface SignInService extends IService<SignIn> {

    void newSignIn(SignIn signIn);
    SignIn getSignIn(long signInID);
}
