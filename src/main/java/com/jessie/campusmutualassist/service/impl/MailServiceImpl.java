package com.jessie.campusmutualassist.service.impl;

import com.jessie.campusmutualassist.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Random;
import java.util.Set;

@Service("mailService")
public class MailServiceImpl implements MailService
{
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Resource(name = "findPwTemplate")
    private SimpleMailMessage findPwTemplate;
    @Resource(name = "newNoticeTemplate")
    private SimpleMailMessage newNotificationTemplate;

    @Async
    @Override
    public void sendResetPw(String dest, String theInfo)
    {
        if (dest == null)
        {
            System.out.println("邮箱地址为空");
            return;
        }
        SimpleMailMessage msg = new SimpleMailMessage(findPwTemplate);
        msg.setTo(dest);//接收人
        msg.setText(theInfo);  //这里的邮件内容是 文本类型
        // msg.setCc(cc);// 抄送
        // msg.setBcc(bcc);// 密送
        // msg.setReplyTo(replyTo);// 回复
        // msg.setSentDate(new Date());// 发送时间
        // msg.setSubject(subject);// 主题
        // msg.setFrom(from);// 发送人
        try
        {
            this.mailSender.send(msg);
        } catch (MailException ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    @Async
    @Override
    public void sendNewMessage(String dest, String message)
    {
        if (dest == null)
        {
            System.out.println("邮箱地址为空");
            return;
        }
        SimpleMailMessage msg = new SimpleMailMessage(this.newNotificationTemplate);
        msg.setTo(dest);//接收人
        msg.setText(message);  //这里的邮件内容是 文本类型
        try
        {
            this.mailSender.send(msg);
        } catch (MailException ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    @Async
    @Override
    public void newMessage(String subject, String dest, String theInfo)
    {
        if (dest == null)
        {
            System.out.println("邮箱地址为空");
            return;
        }
        SimpleMailMessage msg = new SimpleMailMessage(this.findPwTemplate);
        msg.setSubject(subject);
        msg.setTo(dest);
        msg.setText(theInfo);
        try
        {
            this.mailSender.send(msg);
        } catch (MailException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public static String getRandomString()
    {
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++)
        {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum))
            {
                // 取得大写字母还是小写字母
                //int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                int choice=65;//默认用大写了，六位重复率应该比较低吧
                val.append((char) (choice + random.nextInt(26)));
            } else
            { // 数字
                val.append(String.valueOf(random.nextInt(10)));
            }
        }
        return val.toString();
    }
    public static String getRandomNumber(int n)
    {
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < n; i++)
        {
            // 输出字母还是数字
                val.append(String.valueOf(random.nextInt(10)));
        }
        return val.toString();
    }
    @Async
    @Override
    public void urgeStu(Set<String> urgeList, String urgeMessage) {
        for(String x:urgeList){
            newMessage(urgeMessage,x+"@fzu.edu.cn","你有一个公告还没确认，快去看啦！");
        }
    }
}
