package com.jessie.campusmutualassist.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.Notice;
import com.jessie.campusmutualassist.entity.NoticeWithFiles;
import com.jessie.campusmutualassist.exception.NoAccessException;
import com.jessie.campusmutualassist.mapper.NoticeMapper;
import com.jessie.campusmutualassist.mapper.NoticePermissionMapper;
import com.jessie.campusmutualassist.service.MailService;
import com.jessie.campusmutualassist.service.NoticeService;
import com.jessie.campusmutualassist.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("noticeService")
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    NoticeMapper noticeMapper;
    @Autowired
    MailService mailService;//未来会整合出一个PushService来
    @Autowired
    NoticePermissionMapper noticePermissionMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    @Cacheable(value = "noticeCache", key = "#classID")
    public List<Notice> getClassPublicNotices(String classID) {
        return noticeMapper.getClassPublicNotices(classID);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "noticeCache", key = "#notice.classID+'*'"),
            @CacheEvict(value = "noticesWithFile", key = "#notice.classID+'*'")
    })
    public void newNotice(Notice notice) {
        noticeMapper.newNotice(notice);
    }

    @Override
    public void newUnPublicNotice(Notice notice, List<String> readers) {
        newNotice(notice);
        noticePermissionMapper.newNoticePermission(notice.getNid(), readers);
    }

    //破案了，是单调的，不能通配
    //成啦！通配成功啦！
    @Override
    public String getClassID(long nid) {
        return noticeMapper.getClassID(nid);
    }

    @Async
    @Override
    public void urge(Set<String> urgeList) {
        for (String x : urgeList) {
            mailService.newMessage("快去看公告啦", x + "@fzu.edu.cn", "你有一个公告还没确认，快去看啦！");
            //暂时写死
        }
    }

    @Override
    @Cacheable(value = "noticeCache", key = "#classID+':'+#num")
    public PageInfo getClassNoticesPage(String classID, int num) {
        PageHelper.startPage(num, 10);
        List<Notice> list = noticeMapper.getClassPublicNotices(classID);
        //System.out.println("getClassNoticesPage没有使用缓存");
        return new PageInfo<>(list);
    }
    //实在不行，可以只缓存第一页，这样效率不会降低太多
    //可以重写evict的方法，来使通配符生效;但是这样，删除缓存的效率会降低，同时把所有数据缓存到Redis里，以那个服务器的内存大小估计承受不住
    //好吧找到了通配的方法，感谢Dalao的无私分享（真让我去读那个源码，我可能头会很痛）

    @Override
    public List<Notice> getClassNotices(String classID) {
        return noticeMapper.getClassNotices(classID);
    }

    @Override
    public List<Notice> getClassUnPublicNotices(String classID, String username) {
        return noticeMapper.getClassUnPublicNotices(classID, username);
    }

    @Override
    public List getUnConfirmedNotices(String classID, String username) {
        List<Notice> needConfirm = noticeMapper.getClassConfirmNotices(classID);
        List<Long> returnList = new ArrayList<>();
        for (Notice notice : needConfirm) {
            if (!redisUtil.sIsMember("class:" + classID + ":type:" + "noticeConfirmed" + ":" + "nid:" + notice.getNid(), username)) {
                returnList.add(notice.getNid());
            }
        }
        return returnList;
    }

    @Override
    public boolean getNoticeDeadLine(long nid) {
        return noticeMapper.getNoticeDeadLine(nid).isAfter(LocalDateTime.now());
    }

    @Override
    public void deleteNotice(long nid, String classID) {
        redisUtil.delete("class:" + classID + ":type:" + "noticeConfirmed" + ":" + "nid:" + nid);
        noticeMapper.deleteNotice(nid);
    }

    @Override
    @Cacheable(value = "noticesWithFile", key = "#classID")
    public List<NoticeWithFiles> getPublicNoticesWithFiles(String classID) {
        return noticeMapper.getPublicNoticesWithFiles(classID);
    }

    @Override
    @Cacheable(value = "noticesWithFile", key = "#classID+'-'+#pageNum")
    public PageInfo getPublicNoticesWithFilesPage(String classID, int pageNum) {
        PageHelper.startPage(pageNum, 10);
        List<NoticeWithFiles> list = noticeMapper.getPublicNoticesWithFiles(classID);
        return new PageInfo<>(list);
    }

    @Override
    @Cacheable(value = "noticeWithFile", key = "#nid")
    public NoticeWithFiles getNoticeWithFile(long nid, String username) throws NoAccessException {
        NoticeWithFiles notice = noticeMapper.getNoticeWithFiles(nid);
        System.out.println(username);
        System.out.println(noticePermissionMapper.isReaderOfNotice(nid, username));
        if (!notice.isPublic() && !noticePermissionMapper.isReaderOfNotice(nid, username)) {
            throw new NoAccessException();
        }
        return notice;
    }

    @Override
    public void addFilesToNotice(long nid, List<Long> fids) {
        noticeMapper.addFilesToNotice(nid, fids);
    }

}
