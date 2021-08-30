package com.jessie.campusmutualassist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jessie.campusmutualassist.entity.UserWechat;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * @Entity com.jessie.campusmutualassist.entity.UserWechat
 */
public interface UserWechatMapper extends BaseMapper<UserWechat> {

    @Insert("insert into user_wechat (username, openID) values (#{username},#{openID})")
    void newOpenID(String username, String openID);

    @Select("select openID from  user_wechat where username=#{username}")
    String getOpenID(String username);

    @Select("select uw.openID from user_wechat uw join stu_selection ss on ss.stuName=uw.username where ss.classID=#{classID}")
    List<String> getClassOpenID(String classID);

    @Delete("delete from user_wechat where username=#{username}")
    void cancelBind(String username);

    List<String> getOpenIDStrings(Set<String> users);
}




