package com.jessie.campusmutualassist;

import com.alibaba.fastjson.JSONObject;
import com.jessie.campusmutualassist.entity.Notice;
import com.jessie.campusmutualassist.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CampusMutualAssistApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class NoticeTest {

    @Autowired
    NoticeService noticeService;
    @Test
    public void testUnPublicInsert(){
        Notice notice=new Notice();
        notice.setTitle("这条部分人不可见");
        notice.setBody("部分人不可见");
        notice.setClassID("CIRD9F");
        notice.setConfirm(false);
        notice.setPublisher("teacher1");
        notice.setPublic(false);
        notice.setType("其他");
        notice.setNid(8);
        notice.setPublishedTime(LocalDateTime.now());
        List<String> list=new ArrayList<>();
        list.add("student2");
        list.add("student3");
        noticeService.newUnPublicNotice(notice,list);
    }
    @Test
    public void TestSaveJson(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("json","object");
        //jsonTest.newJson(jsonObject.toJSONString());//看来只能存JSON罢了,还必须String传进去
    }

}
