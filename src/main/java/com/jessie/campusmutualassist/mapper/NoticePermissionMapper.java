package com.jessie.campusmutualassist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jessie.campusmutualassist.entity.NoticePermission;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity com.jessie.campusmutualassist.entity.NoticePermission
 */
public interface NoticePermissionMapper extends BaseMapper<NoticePermission> {

    void newNoticePermission(long nid, List<String> list);

    @Select("select * from notice_permission where nid=#{nid}")
    List<NoticePermission> getAllByNid(long nid);


}




