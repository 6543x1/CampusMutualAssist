package com.jessie.campusmutualassist.config;


import com.jessie.campusmutualassist.service.NoticeService;
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
        }
        else{
            log.info("Detected Expired Key: "+theInfo);
        }



    }
}