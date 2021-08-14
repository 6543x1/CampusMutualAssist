package com.jessie.campusmutualassist.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    @CacheEvict(value = "noticeCache",key = "#notice.classID+'*'")//这个Key是通配的吗？还是...可以删除？
    public void newNotice(Notice notice) {
        noticeMapper.newNotice(notice);
    }
    //破案了，是单调的，不能通配
    //成啦！通配成功啦！
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

    @Override
    @Cacheable(value = "noticeCache",key="#classID+'-'+#num")
    public PageInfo getClassNoticesPage(String classID, int num) {
        PageHelper.startPage(num,1);
        List<Notice> list=noticeMapper.getClassNotices(classID);
        System.out.println("getClassNoticesPage没有使用缓存");
        return new PageInfo<>(list);
    }
    //实在不行，可以只缓存第一页，这样效率不会降低太多
    //可以重写evict的方法，来使通配符生效;但是这样，删除缓存的效率会降低，同时把所有数据缓存到Redis里，以那个服务器的内存大小估计承受不住
    //好吧找到了通配的方法，感谢Dalao的无私分享（真让我去读那个源码，我可能头会很痛）
}
