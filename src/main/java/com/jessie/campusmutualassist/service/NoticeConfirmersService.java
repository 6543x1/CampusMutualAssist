package com.jessie.campusmutualassist.service;

import com.jessie.campusmutualassist.entity.NoticeConfirmers;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.scheduling.annotation.Async;

/**
 *
 */
public interface NoticeConfirmersService extends IService<NoticeConfirmers> {

    @Async
    void newConfirmers(long nid, String data);
}
