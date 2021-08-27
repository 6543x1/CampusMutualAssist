package com.jessie.campusmutualassist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jessie.campusmutualassist.entity.SignIn;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity com.jessie.campusmutualassist.Signin
 */
public interface SignInMapper extends BaseMapper<SignIn> {

    @Insert("insert into signin (title, signKey, classID, signedNum, totalNum,deadLine,signType,publisher) VALUES (#{title},#{signKey},#{classID},#{signedNum},#{totalNum},#{deadLine},#{signType},#{publisher})")
    @Options(useGeneratedKeys = true,keyProperty = "signID",keyColumn = "signID")
    void newSignIn(SignIn signIn);
    //备注：时间交给mysql自己生成了
    @Select("select * from signin where signID=#{signID}")
    SignIn getSignIn(long signID);

    @Select("select signID, title,classID, deadLine,signType,publishedTime,publisher from signin where classID=#{classID}")
    List<SignIn> getStuSignIn(String classID);

    @Select("select signID from signin where classID=#{classID} and deadLine>NOW()")//说实话签到三天没签到还是算了吧
    Long[] getClassSignInID(String classID);

    @Delete("delete from signin where signID=#{signID}")
    void deleteBySignID(long signID);
}




