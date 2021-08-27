package com.jessie.campusmutualassist.wechatMsgHandler;

import com.alibaba.fastjson.JSON;
import com.jessie.campusmutualassist.mapper.UserWechatMapper;
import com.jessie.campusmutualassist.utils.RedisUtil;
import com.jessie.campusmutualassist.wechatMsgBuilder.TextBuilder;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class MsgHandler extends AbstractHandler {
    @Autowired
    UserWechatMapper userWechatMapper;
    @Autowired
    RedisUtil redisUtil;
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wechatService,
                                    WxSessionManager sessionManager) {

        if (!wxMessage.getMsgType().equals(XmlMsgType.EVENT)) {
            //TODO 可以选择将消息保存到本地
            System.out.println("服务器收到的消息内容是："+wxMessage.getContent());
        }

        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
        try {
            if (StringUtils.startsWithAny(wxMessage.getContent(), "你好")) {
                return new TextBuilder().build("你好",wxMessage,wechatService);
            }
            if(wxMessage.getContent().matches("绑定[0-9a-zA-Z]{6}")){
              //  System.out.println("绑定码"+wxMessage.getContent().substring(2));
                String thisUserOpenID=wxMessage.getFromUser();
                String username=redisUtil.get("type:"+"wechatBind:"+"key:"+wxMessage.getContent().substring(2));
                userWechatMapper.newOpenID(username,thisUserOpenID);
                return new TextBuilder().build("绑定成功",wxMessage,wechatService);
            }
            if(wxMessage.getContent().matches("解绑[0-9a-zA-Z]{6}")){
                //System.out.println("绑定码"+wxMessage.getContent().substring(2));
                String thisUserOpenID=wxMessage.getFromUser();
                String username=redisUtil.get("type:"+"wechatBind:"+"key:"+wxMessage.getContent().substring(2));
                userWechatMapper.cancelBind(username);
                return new TextBuilder().build("解绑成功",wxMessage,wechatService);
            }
            if(StringUtils.startsWithAny(wxMessage.getContent(),"绑定")){
                System.out.println("绑定码"+wxMessage.getContent().substring(2));
                return new TextBuilder().build("绑定失败，请检查格式是否正确",wxMessage,wechatService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //TODO 组装回复消息
        String content = "收到信息内容：" + JSON.toJSONString(wxMessage);

        return new TextBuilder().build(content, wxMessage, wechatService);

    }

}
