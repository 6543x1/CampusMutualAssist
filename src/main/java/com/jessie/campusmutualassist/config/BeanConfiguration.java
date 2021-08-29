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
    @Bean(name = "textSafeMap")
    public HashMap<String,String> textSafeMap()
    {
        HashMap<String,String> hashMap=new HashMap<String,String>(){
            {
                put("spam","垃圾信息");
                put("ad","广告");
                put("politics","涉政信息");
                put("terrorism","暴恐");
                put("abuse","辱骂");
                put("porn","色情内容");
                put("flood","灌水");
                put("contraband","违禁信息");
                put("meaningless","无意义信息");
                put("customized","违反本应用的规定");
            }
        };
        return hashMap;
    }
    @Bean(name = "textSafeDangerMap")
    public HashMap<String,String> textSafeDangerMap()
    {
        HashMap<String,String> hashMap=new HashMap<String,String>(){
            {
                put("terrorism","暴恐");
                put("abuse","辱骂");
                put("customized","违反本应用的规定");
                put("porn","色情或辱骂");//你知道为什么我要把这个加进来吗，因为在我测试“我干死你的妈这句话的时候...给我判定色情？”
            }
        };
        return hashMap;
    }


//如果直接以Bean方式注入converter，顺序无法确定
    //这也是为什么出现\的原因之一


}
