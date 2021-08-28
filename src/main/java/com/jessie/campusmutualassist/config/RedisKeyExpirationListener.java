package com.jessie.campusmutualassist.config;


import com.alibaba.fastjson.JSONObject;
import com.jessie.campusmutualassist.service.NoticeConfirmersService;
import com.jessie.campusmutualassist.service.NoticeService;
import com.jessie.campusmutualassist.service.SigninSignedService;
import com.jessie.campusmutualassist.service.VoteVotersService;
import com.jessie.campusmutualassist.service.impl.PushService;
import com.jessie.campusmutualassist.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * 主要作用就是:接收过期的redis消息,获取到key,key就是订单号,然后去更新订单号的状态(说明一下:用户5分钟不支付的话取消用户的订单)
 */
@Slf4j
@Transactional
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener
{



    @Autowired
    RedisUtil redisUtil;
    @Autowired
    NoticeService noticeService;
    @Autowired
    PushService pushService;
    @Autowired
    NoticeConfirmersService noticeConfirmersService;
    @Autowired
    VoteVotersService voteVotersService;
    @Autowired
    SigninSignedService signinSignedService;


    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer)
    {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern)
    {
        //提示 所有的过期应该加上一个随机的时间（比如30分钟的，可以随机加/减个一分钟以内的任意时间，把数据分散过期，影响不会太大）
        //不过我redis配置的是访问这条数据才去检查是否过期，所以只要没用到，影响不会太大..吧
        //example
//        scheduledTask:"+"noticeUrge:"+"classID:"+classID+":"+"nid"+":"+notice.getNid()
        String theInfo = message.toString();
        String[] Key = theInfo.split(":");//以:分割开信息,据说这是redis的规范
        String type = Key[1];
        if("noticeUrge".equals(type)){
            String classID=Key[3];
            int nid=Integer.parseInt(Key[5]);
            Set<String> notConfirmed = redisUtil.sDifference("class:" + classID + ":type:" + "noticeConfirmed" + ":" + "nid:" + nid, "class:" + classID + ":" + "type:" + "members");
            noticeService.urge(notConfirmed);
            pushService.pushUrgeWechatMessage(notConfirmed,"公告","老师催你了去读公告了！","无摘要");
            noticeConfirmersService.newConfirmers(nid, JSONObject.toJSONString(redisUtil.get("class:" + classID + ":type:" + "noticeConfirmed" + ":" + "nid:" + nid)));
            //超时的...先不加进去吧
        }
        else if("VoteDeadLine".equals(type)){
            String classID=Key[3];
            int vid=Integer.parseInt(Key[5]);
            voteVotersService.newVoters(vid,redisUtil.get("class:" + classID + ":" + "type:" + "members"));
            //累了，只先保存投了的人吧
            //选项对应的人不存了不存了
        }
        else if("signInExpire".equals(type)){
            String classID=Key[3];
            int signID=Integer.parseInt(Key[5]);
            Set<String> SignInList = redisUtil.sDifference("class:" + classID + ":type:" + "signIn" + ":" + "signId:"+signID,"class:" + classID + ":" + "type:" + "members");
            signinSignedService.newSigned(signID, JSONObject.toJSONString(SignInList));
        }
        else{
            log.info("Detected Expired Key: "+theInfo);
        }



    }
}