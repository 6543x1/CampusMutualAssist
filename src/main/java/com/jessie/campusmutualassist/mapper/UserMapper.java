package com.jessie.campusmutualassist.mapper;


import com.jessie.campusmutualassist.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Mapper
public interface UserMapper
{

    @Insert("insert into user (username,password,status,role,evaluation,realName) values (#{username},#{password},#{status},#{role},#{evaluation},#{realName})")
    @Options(useGeneratedKeys = true, keyProperty = "uid", keyColumn = "uid")
    void saveUser(User user);
    @Update("update user set mobileNumber=#{mobileNumber} where username=#{username}")
    void savePhoneNumber(@Param("username") String username,@Param("mobile") String mobileNumber);
    @Select("select count(1) from user where username=#{username}")
    boolean queryUser(String username);

    @Update("update user set img_path=#{img_path} where uid=#{uid} ")
    void saveImg(User user);


    @Select("select * from user where username= #{username}")
    User getUser(String username);
    //上面这个方法可以优化一下，只要Username和Password用于登录
    User getNoPasswordUser(String username);

    @Select("select realName from user where username=#{username}")
    String getNickNameByUsername(String username);
    @MapKey("username")
    List<Map<String,String>> getRealNameWithUsername(Set<String> usernameSet);
    //mybatis这个东西不能直接返回Map，必须用List封装Map，然后再把里面Map一个个取出来，放入新的Map...合着你这Map当Pair使用是吧

    @Select("select mailAddr from user where username=#{username}")
    String getMailAddrByUsername(String username);

    @Select("select mailAddr from user where uid=#{uid}")
    String getMailAddrByUid(int uid);

    @Select("select img_path from user where uid=#{uid}")
    String getImgPathByUid(int uid);

    @Select("select * from user where uid= #{uid}")
    User getUserByUid(int uid);//方法重载会报错。。。

    @Select("select uid from user where username=#{username}")
    int getUid(String username);

    @Update("update user set password=#{password} where username=#{username}")
    void editPassword(@Param("username") String username, @Param("password") String password);

    @Update("update user set mailAddr=#{mailAddr} where uid=#{uid}")
    void setMailAddr(@Param("uid") int uid, @Param("mailAddr") String mailAddr);

    @Update("update user set status=#{status} where username=#{username}")
    void setStatus(@Param("username") String username, @Param("status") int status);

    @Select("select status from user where uid=#{uid}")
    int getStatus(int uid);

    @Update("update user set realName=#{nickName} where username=#{username}")
    void setRealName(String username);

    @Update("update user set evaluation=#{evaluation} where uid=#{uid}")
    void updateEvaluation(@Param("uid") int uid, @Param("evaluation") int evaluation);

    @Select("select password from user where username=#{username}")
    String getPassword(String username);




}
