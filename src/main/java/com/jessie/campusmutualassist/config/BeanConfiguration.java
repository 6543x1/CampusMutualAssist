package com.jessie.campusmutualassist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;

import java.util.HashMap;

/*
 * 本类用于设置一些自定义的bean
 * */
@Configuration
public class BeanConfiguration
{
    @Bean(name="theImgSuffix")
    public HashMap<Integer,String> getImgSuffix(){
        HashMap<Integer,String> map=new HashMap<>();
        map.put(0,"jpg");
        map.put(1,"png");
        map.put(2,"heic");
        map.put(3,"gif");
        map.put(4,"jpeg");
        return map;
    }
    @Bean(name= "findPwTemplate")
    public SimpleMailMessage simpleMailMessage(){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom("6543x1@outlook.com");
        simpleMailMessage.setSubject("找回密码（请勿回复）");
        return simpleMailMessage;
    }

    @Bean(name = "newNoticeTemplate")
    public SimpleMailMessage NewOrderMailMessage()
    {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("6543x1@outlook.com");
        simpleMailMessage.setSubject("你收到一条新消息");
        return simpleMailMessage;
    }

//如果直接以Bean方式注入converter，顺序无法确定
    //这也是为什么出现\的原因之一


}
