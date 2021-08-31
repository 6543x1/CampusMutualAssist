package com.jessie.campusmutualassist.mapper;

import com.jessie.campusmutualassist.entity.Notice;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
@Repository
public interface NoticeMapper {
    @Insert("insert into notice (title, body, classID,publisher, publishedTime,deadLine,type,confirm,isPublic) VALUES (#{title},#{body},#{classID},#{publisher},#{publishedTime},#{deadLine},#{type},#{confirm},#{isPublic}) ")
    @Options(useGeneratedKeys = true, keyProperty = "nid", keyColumn = "nid")
    void newNotice(Notice notice);

    @Select("select * from notice where classID=#{classID} order by nid desc")
    List<Notice> getClassNotices(String classID);

    @Select("select * from notice where nid=#{nid}")
    Notice getNotice(long nid);

    @Select("select deadLine  from notice where nid=#{nid}")
    LocalDateTime getNoticeDeadLine(long nid);

    @Select("select * from notice where classID=#{classID} and isPublic=true")
    List<Notice> getClassPublicNotices(String classID);

    @Select("select n.* from notice n join notice_permission np on n.nid =np.nid where n.classID=#{classID} and n.isPublic = false and np.reader =#{username}")
    List<Notice> getClassUnPublicNotices(String classID, String username);

    @Select("select classID from notice where nid=#{nid}")
    String getClassID(long nid);

    @Select("select * from notice where classID=#{classID} and confirm=true")
    List<Notice> getClassConfirmNotices(String classID);

    @Delete("delete from notice where nid=#{nid}")
    void deleteNotice(long nid);
}
