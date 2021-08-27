package com.jessie.campusmutualassist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.Vote;

import java.util.List;

/**
 *
 */
public interface VoteService extends IService<Vote> {
    void newVote(Vote vote);
    List<Vote> getClassVotes(String classID);
    PageInfo getClassVotesPage(String classID,int pageNum);
    Vote getVote(long vid);
    List<Long> getNotVotes(String username, String classID);

    void deleteVote(String classID, long vid);
}
