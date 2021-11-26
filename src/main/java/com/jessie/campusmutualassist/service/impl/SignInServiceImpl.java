package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.SignIn;
import com.jessie.campusmutualassist.service.SignInService;
import com.jessie.campusmutualassist.mapper.SignInMapper;
import com.jessie.campusmutualassist.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;

/**
 *
 */
@Service("signInService")
public class SignInServiceImpl extends ServiceImpl<SignInMapper, SignIn>
        implements SignInService {
    @Autowired
    SignInMapper signInMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    @CacheEvict(value = "classSignIn", key = "#signIn.classID+'*'")
    public void newSignIn(SignIn signIn) {
        signInMapper.newSignIn(signIn);
    }

    @Override
    @Cacheable(value = "classSignIn", key = "#signInID")
    public SignIn getSignIn(long signInID) {
        return signInMapper.getSignIn(signInID);
    }

    @Override
    @Cacheable(value = "classSignIn", key = "#classID")
    public List<SignIn> getStuSignIn(String classID) {
        List<SignIn> list = signInMapper.getStuSignIn(classID);
        for (SignIn signIn : list) {
            if (redisUtil.sIsMember("class:" + classID + ":type:" + "signIn" + ":" + "signId:" + signIn.getSignID(), getCurrentUsername())) {
                signIn.setSigned(true);
            } else {
                signIn.setSigned(false);
            }
        }
        return list;
    }

    @Override
    @Cacheable(value = "classSignIn", key = "#classID+'-'+#pageNum")
    public PageInfo getStuSignInPage(String classID, int pageNum) {
        PageHelper.startPage(pageNum, 10);
        List<SignIn> list = signInMapper.getStuSignIn(classID);
        return new PageInfo<>(list);
    }

    @Override
    @Cacheable(value = "classSignIn", key = "#classID+':'+#username")
    public List getMyNotSign(String classID, String username) {
        Long[] classSignInID = signInMapper.getClassSignInID(classID);
        ArrayList<Long> arrayList = new ArrayList<>();
        for (long signID : classSignInID) {
            if (redisUtil.sIsMember("class:" + classID + ":type:" + "signIn" + ":" + "signId:" + signID, username)) {
                arrayList.add(signID);
            }
        }
        return arrayList;
    }

    @Override
    public void deleteSignIn(String classID, long signID) {
        redisUtil.delete("class:" + classID + ":type:" + "signIn" + ":" + "signID:" + signID);
        signInMapper.deleteBySignID(signID);
    }
}





