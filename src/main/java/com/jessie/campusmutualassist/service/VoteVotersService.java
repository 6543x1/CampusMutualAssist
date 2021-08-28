package com.jessie.campusmutualassist.service;

import com.jessie.campusmutualassist.entity.VoteVoters;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.scheduling.annotation.Async;

/**
 *
 */
public interface VoteVotersService extends IService<VoteVoters> {

    @Async
    void newVoters(long vid, String data);
}
