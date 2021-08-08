package com.jessie.campusmutualassist.service;

import com.jessie.campusmutualassist.entity.Vote;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface VoteService extends IService<Vote> {
    void newVote(Vote vote);
}
