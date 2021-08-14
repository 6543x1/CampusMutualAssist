package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jessie.campusmutualassist.entity.SignIn;
import com.jessie.campusmutualassist.service.SignInService;
import com.jessie.campusmutualassist.mapper.SignInMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service("signInService")
public class SignInServiceImpl extends ServiceImpl<SignInMapper, SignIn>
    implements SignInService {
    @Autowired
    SignInMapper signInMapper;

    @Override
    public void newSignIn(SignIn signIn) {
        signInMapper.newSignIn(signIn);
    }

    @Override
    public SignIn getSignIn(long signInID) {
        return signInMapper.getSignIn(signInID);
    }
}




