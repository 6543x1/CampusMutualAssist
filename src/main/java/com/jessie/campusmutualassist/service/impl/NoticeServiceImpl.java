package com.jessie.campusmutualassist.service.impl;

import com.jessie.campusmutualassist.entity.Notice;
import com.jessie.campusmutualassist.mapper.NoticeMapper;
import com.jessie.campusmutualassist.service.MailService;
import com.jessie.campusmutualassist.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("noticeService")
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    NoticeMapper noticeMapper;
    @Autowired
    MailService mailService;//未来会整合出一个PushService来
    @Override
    @Cacheable(value = "noticeCache",key="#classID")
    public List<Notice> getClassNotices(String classID) {
        System.out.println("getClassNotices被调用");
        return noticeMapper.getClassNotices(classID);
    }

    @Override
    @CacheEvict(value = "noticeCache",key = "#notice.classID")
    public void newNotice(Notice notice) {
        noticeMapper.newNotice(notice);
    }

    @Override
    public String getClassID(int nid) {
       return noticeMapper.getClassID(nid);
    }
    @Async
    @Override
    public void urge(Set<String> urgeList) {
        for(String x:urgeList){
            mailService.newMessage("快去看公告啦",x+"@fzu.edu.cn","你有一个公告还没确认，快去看啦！");
            //暂时写死
        }
    }
}
