package com.jessie.campusmutualassist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jessie.campusmutualassist.entity.StuPointsDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity com.jessie.campusmutualassist.entity.StuPointsDetail
 */
public interface StuPointsDetailMapper extends BaseMapper<StuPointsDetail> {

    @Insert("insert into stu_points_detail (classID, operator, target, points, reason) VALUES (#{classID},#{operator},#{target},#{points},#{reason})")
    void newDetail(StuPointsDetail stuPointsDetail);

    @Select("select * from stu_points_detail where classID=#{classID}")
    List<StuPointsDetail> classDetails(String classID);

    @Select("select * from stu_points_detail where classID=#{classID} and target=#{target}")
    List<StuPointsDetail> stuClassDetails(String classID, String target);

    @Delete("delete from stu_points_detail where classID=#{classID} and time<=NOW() ")
    void deleteOldItems(String classID);

}




