package com.jessie.campusmutualassist.service;

import com.jessie.campusmutualassist.entity.SigninSigned;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.scheduling.annotation.Async;

/**
 *
 */
public interface SigninSignedService extends IService<SigninSigned> {

    @Async
    void newSigned(long signID, String data);
}
