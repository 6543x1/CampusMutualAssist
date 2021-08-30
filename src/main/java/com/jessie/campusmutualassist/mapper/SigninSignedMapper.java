package com.jessie.campusmutualassist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jessie.campusmutualassist.entity.SigninSigned;
import org.apache.ibatis.annotations.Insert;

/**
 * @Entity com.jessie.campusmutualassist.entity.SigninSigned
 */
public interface SigninSignedMapper extends BaseMapper<SigninSigned> {
    @Insert("insert into signin_signed (signID, signed) VALUES (#{signID},#{signed})")
    public void saveSigned(long signID, String signed);
}




