package com.jessie.campusmutualassist.service.impl;

import com.jessie.campusmutualassist.mapper.UserWechatMapper;
import com.jessie.campusmutualassist.service.MailService;
import com.jessie.campusmutualassist.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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

    public void pushUrgeWechatMessage(Set<String> users,String type,String title,String body){
        List<String> openIDStrings = userWechatMapper.getOpenIDStrings(users);
        for(String x:openIDStrings){
            wechatService.pushUrgeMessage(x,type,title,body);
        }
    }
    public void pushSocketMessage(Set<String> users,String message){
        for(String x:users){
            webSocketServer.sendTo(message,x);
        }
    }
}
