package com.jessie.campusmutualassist;

import com.jessie.campusmutualassist.mapper.UserWechatMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CampusMutualAssistApplication.class)
public class WechatTest {
    @Autowired
    UserWechatMapper userWechatMapper;

    @Test
    public void testReadAndSave(){
        List<String> list=userWechatMapper.getOpenIDStrings(Collections.singleton(""));
        System.out.println(list);
    }

}
