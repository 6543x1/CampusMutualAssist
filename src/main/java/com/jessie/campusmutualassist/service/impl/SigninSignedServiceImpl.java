package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jessie.campusmutualassist.entity.SigninSigned;
import com.jessie.campusmutualassist.mapper.SigninSignedMapper;
import com.jessie.campusmutualassist.service.SigninSignedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service("SigninSignedService")
public class SigninSignedServiceImpl extends ServiceImpl<SigninSignedMapper, SigninSigned>
        implements SigninSignedService {
    @Autowired
    SigninSignedMapper signinSignedMapper;

    @Async
    @Override
    public void newSigned(long signID, String data) {
        signinSignedMapper.saveSigned(signID, data);
    }
}




