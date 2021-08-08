package com.jessie.campusmutualassist.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@ApiOperation(value = "测试多线程的类")
@RestController
@RequestMapping("/test")
public class MultiThreadController {
    @RequestMapping("/testMultiSave")
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
}
