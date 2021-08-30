package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jessie.campusmutualassist.entity.NoticeConfirmers;
import com.jessie.campusmutualassist.service.NoticeConfirmersService;
import com.jessie.campusmutualassist.mapper.NoticeConfirmersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service("NoticeConfirmersService")
public class NoticeConfirmersServiceImpl extends ServiceImpl<NoticeConfirmersMapper, NoticeConfirmers>
        implements NoticeConfirmersService {
    @Autowired
    NoticeConfirmersMapper noticeConfirmersMapper;

    @Async
    @Override
    public void newConfirmers(long nid, String data) {
        noticeConfirmersMapper.saveConfirmers(nid, data);
    }
}




