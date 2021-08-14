package com.jessie.campusmutualassist.service;

import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.Notice;

import java.util.List;
import java.util.Set;

public interface NoticeService {
    void newNotice(Notice notice);
    List<Notice> getClassNotices(String classID);
    String getClassID(int nid);
    void urge(Set<String> urgeList);
    PageInfo getClassNoticesPage(String classID, int num);
}
