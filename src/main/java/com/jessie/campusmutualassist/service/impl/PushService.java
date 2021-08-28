package com.jessie.campusmutualassist.service.impl;

import com.jessie.campusmutualassist.mapper.UserWechatMapper;
import com.jessie.campusmutualassist.service.MailService;
import com.jessie.campusmutualassist.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("pushService")
public class PushService {

    @Autowired
    MailService mailService;
    @Autowired
    WechatService wechatService;
    @Resource
    WebSocketServer webSocketServer;
    @Autowired
    UserWechatMapper userWechatMapper;
    @Async
    public void pushUrgeWechatMessage(Set<String> users,String type,String title,String body){
        List<String> openIDStrings = userWechatMapper.getOpenIDStrings(users);
        for(String x:openIDStrings){
            wechatService.pushUrgeMessage(x,type,title,body);
        }
    }
    @Async
    public void pushSocketMessage(Set<String> users,String message){
        for(String x:users){
            webSocketServer.sendTo(message,x);
        }
    }
    @Async
    public void pushWechatMessage(Set<String> users,String message){
        List<String> openIDStrings = userWechatMapper.getOpenIDStrings(users);
        Map<String,String> map=new HashMap<String,String>(){{
            put("message",message);
        }};
        openIDStrings.forEach((openID)->{
            wechatService.sendTemplateMsg(openID,"o3Gq7E4-wadrZ7xy-6h-zBwd-VzgmAyo5byVhcVw-nU",map);
        });
    }
}
