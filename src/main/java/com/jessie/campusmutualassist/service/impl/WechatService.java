package com.jessie.campusmutualassist.service.impl;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("wechatService")
public class WechatService {
    @Autowired
    WxMpService wxService;



    public void pushUrgeMessage(String targetOpenID,String type,String title,String body){//推送老师催促消息
        HashMap<String,String> map=new HashMap<String,String>(){{
           put("type",type);
           put("title",title);
           put("body",body);
        }};
        sendTemplateMsg(targetOpenID,"yCovAV6rNyrmUpJ1RYAtEKFdZiQmrpp9Ksom5xmAeNU",map);
    }
    public String sendTemplateMsg(String targetOpenID, String templateID, Map<String,String> argus){
        WxMpTemplateMessage wxMpTemplateMessage=WxMpTemplateMessage.builder().toUser(targetOpenID).templateId(templateID).build();
        for(Map.Entry<String,String> entry:argus.entrySet()){
        wxMpTemplateMessage.addData(new WxMpTemplateData(entry.getKey(),entry.getValue(),"000000"));}
        try {
            wxService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

}
