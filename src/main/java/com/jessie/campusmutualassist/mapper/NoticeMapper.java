package com.jessie.campusmutualassist.mapper;

import com.jessie.campusmutualassist.entity.Notice;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NoticeMapper {
    @Insert("insert into notice (title, body, classID,publisher, publishedTime) VALUES (#{title},#{body},#{classID},#{publisher},#{publishedTime}) ")
    @Options(useGeneratedKeys = true, keyProperty = "nid", keyColumn = "nid")
    void newNotice(Notice notice);
    @Select("select * from notice where classID=#{classID}")
    List<Notice> getClassNotices(String classID);

    @Select("select classID from notice where nid=#{nid}")
    String getClassID(int nid);
}
