package com.jessie.campusmutualassist.service;

import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.Notice;
import com.jessie.campusmutualassist.entity.NoticeWithFiles;

import java.util.List;
import java.util.Set;

public interface NoticeService {
    void newNotice(Notice notice);

    void newUnPublicNotice(Notice notice, List<String> readers);

    List<Notice> getClassNotices(String classID);

    List<Notice> getClassPublicNotices(String classID);

    List<Notice> getClassUnPublicNotices(String classID, String username);

    List<Notice> getUnConfirmedNotices(String classID, String username);

    String getClassID(long nid);

    void urge(Set<String> urgeList);

    PageInfo getClassNoticesPage(String classID, int num);

    boolean getNoticeDeadLine(long nid);

    void deleteNotice(long nid, String classID);

    List<NoticeWithFiles> getPublicNoticesWithFiles(String classID);

    NoticeWithFiles getNoticeWithFile(long nid, String username);

    void addFilesToNotice(long nid, List<Long> fids);
}
