package com.jessie.campusmutualassist.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PermissionMapper
{

    @Insert("insert into user_permission (username,permission) values(#{username},#{permission})")
    void setUserPermission(@Param("username") String username, @Param("permission") String permission);

    @Select("select 1 from user_permission where username=#{username} and permission=#{permission}")
    boolean queryUserPermission(String username,String permission);//注意重复数据好像会导致false

    @Select("select permission from user_permission where username=#{username}")
    List<String> getAllUserPermissions(String username);

    @Delete("delete from user_permission where username=#{username} and permission=#{permission}")
    void deletePermission(@Param("username") String username, @Param("permission") String permission);


}

