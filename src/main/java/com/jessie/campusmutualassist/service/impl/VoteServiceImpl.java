package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jessie.campusmutualassist.entity.Vote;
import com.jessie.campusmutualassist.service.VoteService;
import com.jessie.campusmutualassist.mapper.VoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class VoteServiceImpl extends ServiceImpl<VoteMapper, Vote>
    implements VoteService{
    @Autowired
    VoteMapper voteMapper;
    @Override
    public void newVote(Vote vote) {
        voteMapper.newVote(vote);
    }
}




