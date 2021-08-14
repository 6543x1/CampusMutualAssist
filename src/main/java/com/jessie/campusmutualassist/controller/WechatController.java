package com.jessie.campusmutualassist.controller;

import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.utils.RedisUtil;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpMenuServiceImpl;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.jessie.campusmutualassist.service.impl.MailServiceImpl.getRandomString;
import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;

@RestController
@RequestMapping("/wechat")
public class  WechatController {
    @Autowired
    WxMpService wechatService;
    @Autowired
    WxMpConfigStorage wxMpConfigStorage;
    @Autowired
    RedisUtil redisUtil;
    @Resource(name = "messageRouter")
    WxMpMessageRouter messageRouter;
    static final Random random=new Random();
    @PostMapping(value = "/bind",produces = "application/json;charset=UTF-8")
    public Result bindWechat(){
        String key= getRandomString();
        redisUtil.set("type:"+"wechatBind:"+"key:"+key,getCurrentUsername(),5*60+random.nextInt(10));
        return Result.success("已经获取到Key，请到公众号中回复绑定+key来绑定,五分钟内有效",key);
    }
    @GetMapping(value = "/accessToken",produces = "application/json;charset=UTF-8")
    public Result accessToken(){
        try {
            System.out.println(wechatService.getAccessToken());
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return Result.success("success");
    }
    @GetMapping(value = "/sendTemplate",produces = "application/json;charset=UTF-8")
    public Result sendTemplate(){
        WxMpTemplateMessage wxMpTemplateMessage=WxMpTemplateMessage.builder().toUser("oUqup56c23bBkqGBcCLOvbkneM80").templateId("klDAhI9LH25atQsT07gUnXE4Z5i7rMmUwY0Pi9Jw6Rg").build();
        wxMpTemplateMessage.addData(new WxMpTemplateData("content","Hello world","000000"));
        try {
            wechatService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return Result.error("错误");
        }
        return Result.success("成功");
        //success
        //代码里可以设置出现异常自动发送到指定邮箱
    }

    @GetMapping(value = "/quickStart",produces = "application/json;charset=UTF-8")
    public Result quickStart() throws Exception{
//        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
//        config.setAppId("wx11ca935e13bb174a"); // 设置微信公众号的appid
//        config.setSecret("597869e8058d0eb033b16f7b81424203"); // 设置微信公众号的app corpSecret
//        config.setToken("wechatTestCAM"); // 设置微信公众号的token
//        //config.setAesKey("..."); // 设置微信公众号的EncodingAESKey
//        //wxMpService.setWxMpConfigStorage(config);
//        DefaultApacheHttpClientBuilder clientBuilder = DefaultApacheHttpClientBuilder.get();
//        clientBuilder.setConnectionRequestTimeout(10000);//从连接池获取链接的超时时间(单位ms)
//        clientBuilder.setConnectionTimeout(10000);//建立链接的超时时间(单位ms)
//        clientBuilder.setSoTimeout(10000);//连接池socket超时时间(单位ms)
//        clientBuilder.setIdleConnTimeout(10000);//空闲链接的超时时间(单位ms)
//        clientBuilder.setCheckWaitTime(1000);//空闲链接的检测周期(单位ms)
//        clientBuilder.setMaxConnPerHost(50);//每路最大连接数
//        clientBuilder.setMaxTotalConn(100);//连接池最大连接数
//        //clientBuilder.setUserAgent(..)//HttpClient请求时使用的User Agent
//        config.setApacheHttpClientBuilder(clientBuilder);
//        wxService.setWxMpConfigStorage(config);
//        System.out.println(wxService.getAccessToken());
        String openid = "oUqup56c23bBkqGBcCLOvbkneM80";
        WxMpKefuMessage message = WxMpKefuMessage.TEXT().toUser(openid).content("Hello World").build();
        wechatService.getKefuService().sendKefuMessage(message);
        return Result.success("success");//届到了！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
    }
    @GetMapping(value = "/testMenu",produces = "application/json;charset=UTF-8")
    public Result test(){
        String accessToken="";
        try {
            accessToken= wechatService.getAccessToken();
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        WxMpMenuServiceImpl wxMpMenuService=new WxMpMenuServiceImpl(wechatService);
        WxMenu wxMenu=new WxMenu();
        WxMenuButton wxMenuButton=new WxMenuButton();
        wxMenuButton.setType("click");
        wxMenuButton.setName("绑定账号");
        wxMenuButton.setKey("relative_user");
        List<WxMenuButton> wxMenuButtonList=new ArrayList<>();
        wxMenuButtonList.add(wxMenuButton);
        wxMenu.setButtons(wxMenuButtonList);
        try {
        wxMpMenuService.menuCreate(wxMenu);}catch (Exception e){
            e.printStackTrace();
        }
        wechatService.setMenuService(wxMpMenuService);

        return Result.success("创建菜单按钮成功");
    }
    @RequestMapping(value = "/testToken",produces = "text/plain;charset=UTF-8",method = {RequestMethod.GET,RequestMethod.POST})
    public void receiveMsg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        if (!wechatService.checkSignature(timestamp, nonce, signature)) {
            // 消息签名不正确，说明不是公众平台发过来的消息
            response.getWriter().println("非法请求");
            return;
        }

        String echostr = request.getParameter("echostr");
        if (StringUtils.isNotBlank(echostr)) {
            // 说明是一个仅仅用来验证的请求，回显echostr
            response.getWriter().println(echostr);
            return;
        }

        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ?
                "raw" :
                request.getParameter("encrypt_type");

        WxMpXmlMessage inMessage = null;

        if ("raw".equals(encryptType)) {
            // 明文传输的消息
            inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
        } else if ("aes".equals(encryptType)) {
            // 是aes加密的消息
            String msgSignature = request.getParameter("msg_signature");
            inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), wxMpConfigStorage, timestamp, nonce, msgSignature);
        } else {
            response.getWriter().println("不可识别的加密类型");
            return;
        }
        System.out.println(inMessage.getContent());
        WxMpXmlOutMessage outMessage = messageRouter.route(inMessage);
        //System.out.println(outMessage.getMsgType());
        if (outMessage != null) {
            if ("raw".equals(encryptType)) {
                response.getWriter().write(outMessage.toXml());
            } else if ("aes".equals(encryptType)) {
                response.getWriter().write(outMessage.toEncryptedXml(wxMpConfigStorage));
            }

      return;
    }
    }

    @GetMapping(value = "/testToken2",produces = "text/plain;charset=UTF-8")
    //微信接口测试
    public String test(@RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("echostr") String echostr) {

        //排序
        String TOKEN = "wechatTestCAM";
        String sortString = sort(TOKEN, timestamp, nonce);
        //加密
        String myString = sha1(sortString);
        //校验
        if (myString != null && !"".equals(myString) && myString.equals(signature)) {
            System.out.println("签名校验通过");
            System.out.println(echostr);
            //如果检验成功原样返回echostr，微信服务器接收到此输出，才会确认检验完成。
            return echostr;
        } else {
            System.out.println("签名校验失败");
            return "";
        }
    }

    public String sort(String token, String timestamp, String nonce) {
        String[] strArray = {token, timestamp, nonce};
        Arrays.sort(strArray);
        StringBuilder sb = new StringBuilder();
        for (String str : strArray) {
            sb.append(str);
        }

        return sb.toString();
    }

    public String sha1(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
