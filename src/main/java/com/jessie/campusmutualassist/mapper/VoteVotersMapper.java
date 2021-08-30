package com.jessie.campusmutualassist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jessie.campusmutualassist.entity.VoteVoters;
import org.apache.ibatis.annotations.Insert;

/**
 * @Entity com.jessie.campusmutualassist.entity.VoteVoters
 */
public interface VoteVotersMapper extends BaseMapper<VoteVoters> {
    @Insert("insert into vote_voters (vid, voters) VALUES (#{vid},#{voters})")
    public void saveVoters(long vid, String voters);
}




