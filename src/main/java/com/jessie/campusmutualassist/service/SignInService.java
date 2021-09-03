package com.jessie.campusmutualassist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.SignIn;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 *
 */
public interface SignInService extends IService<SignIn> {

    void newSignIn(SignIn signIn);

    SignIn getSignIn(long signInID);

    List<SignIn> getStuSignIn(String classID);

    @Cacheable(value = "classSignIn", key = "#classID+'-'+#pageNum")
    PageInfo getStuSignInPage(String classID, int pageNum);

    List getMyNotSign(String classID, String username);

    void deleteSignIn(String classID, long signID);
}
