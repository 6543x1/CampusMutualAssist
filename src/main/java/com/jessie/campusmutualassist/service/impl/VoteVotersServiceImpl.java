package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jessie.campusmutualassist.entity.VoteVoters;
import com.jessie.campusmutualassist.mapper.VoteVotersMapper;
import com.jessie.campusmutualassist.service.VoteVotersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service("VoteVotersService")
public class VoteVotersServiceImpl extends ServiceImpl<VoteVotersMapper, VoteVoters>
        implements VoteVotersService {
    @Autowired
    VoteVotersMapper voteVotersMapper;

    @Async
    @Override
    public void newVoters(long vid, String data) {
        voteVotersMapper.saveVoters(vid, data);
    }
}




