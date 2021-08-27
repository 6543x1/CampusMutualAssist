package com.jessie.campusmutualassist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jessie.campusmutualassist.entity.Vote;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity com.jessie.campusmutualassist..Vote
 */
public interface VoteMapper extends BaseMapper<Vote> {
    @Insert("insert into vote (classID, title, limitation, publishedTime, anonymous,publisher,deadLine) VALUES (#{classID},#{title},#{limitation},#{publishedTime},#{anonymous},#{publisher},#{deadLine})")
    @Options(useGeneratedKeys = true, keyProperty = "vid", keyColumn = "vid")
    void newVote(Vote vote);

    @Select("select * from vote where  classID=#{classID}")
    List<Vote> getClassVotes(String classID);

    @Select("select vid from vote where classID=#{classID} and publishedTime >=date_sub(NOW(),interval 14 day)  ")
    Long[] getClassVotesID(String classID);

    @Select("select * from vote where vid=#{vid}")
    Vote getVote(long vid);

    @Delete("delete from  vote where vid=#{vid}")
    void deleteVote(long vid);
}




