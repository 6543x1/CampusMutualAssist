package com.jessie.campusmutualassist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jessie.campusmutualassist.entity.SignIn;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/**
 * @Entity com.jessie.campusmutualassist..Signin
 */
public interface SignInMapper extends BaseMapper<SignIn> {

    @Insert("insert into signin (title, signKey, classID, signedNum, totalNum) VALUES (#{title},#{signKey},#{classID},#{signedNum},#{totalNum})")
    @Options(useGeneratedKeys = true,keyProperty = "signID",keyColumn = "signID")
    void newSignIn(SignIn signIn);

    @Select("select * from signin where signID=#{signID}")
    SignIn getSignIn(long signID);

}




