package com.jessie.campusmutualassist;

import com.jessie.campusmutualassist.mapper.UserWechatMapper;
import com.jessie.campusmutualassist.service.impl.PushService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CampusMutualAssistApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WechatTest {
    @Autowired
    UserWechatMapper userWechatMapper;
    @Autowired
    PushService pushService;

    @Test
    public void testReadAndSave(){
        List<String> list=userWechatMapper.getOpenIDStrings(Collections.singleton(""));
        System.out.println(list);
    }

    @Test
    public void testUrgeTemplate(){
        pushService.pushUrgeWechatMessage(Collections.singleton("Jessie"),"投票","关于xxx的投票","无摘要");
    }

}
