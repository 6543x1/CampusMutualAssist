package com.jessie.campusmutualassist.mapper;

import com.jessie.campusmutualassist.entity.NoticeConfirmers;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

/**
 * @Entity com.jessie.campusmutualassist.entity.NoticeConfirmers
 */
public interface NoticeConfirmersMapper extends BaseMapper<NoticeConfirmers> {

    @Insert("insert into notice_confirmers (nid, confirmers) VALUES (#{nid},#{confirmers})")
    public void saveConfirmers(long nid, String confirmers);
}




