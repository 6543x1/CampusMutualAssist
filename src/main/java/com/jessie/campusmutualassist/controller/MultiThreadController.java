package com.jessie.campusmutualassist.controller;

import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.service.NoticeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiOperation(value = "测试多线程的类")
@RestController
@RequestMapping("/test")
public class MultiThreadController {
    @Autowired
    NoticeService noticeService;
    @PostMapping("/testMultiSave")
    public String testMultiSave(){
        String[] Names= {"22haha","33uu","44tt"};
        try {
        for(int i=0;i<50;i++){
            //userTokenService.newUser(i,getRandomString());
        }}catch (Exception e){
            e.printStackTrace();
        }
        return "success";
    }

    @PostMapping(value = "/noticePage",produces = "application/json;charset=UTF-8")
    public PageInfo getNoticeByPage(){
        PageInfo pageInfo=noticeService.getClassNoticesPage("CIRD9F",1);
        return pageInfo;
    }

    @PostMapping(value = "/getPath",produces = "application/json;charset=UTF-8")
    public Result getPath() throws Exception{
        String path = ResourceUtils.getURL("classpath:").getPath();
        return Result.success(path);
    }
}
