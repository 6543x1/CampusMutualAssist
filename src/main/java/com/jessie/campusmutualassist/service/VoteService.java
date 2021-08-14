package com.jessie.campusmutualassist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.Vote;

/**
 *
 */
public interface VoteService extends IService<Vote> {
    void newVote(Vote vote);
    PageInfo<Vote> getClassVotes(String classID,int pageNum);
}
