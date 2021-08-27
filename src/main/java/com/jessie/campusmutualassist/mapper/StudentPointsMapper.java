package com.jessie.campusmutualassist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jessie.campusmutualassist.entity.StuPointsWithRealName;
import com.jessie.campusmutualassist.entity.StudentPoints;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Entity com.jessie.campusmutualassist.entity.StudentPoints
 */
@Repository
@Mapper
public interface StudentPointsMapper extends BaseMapper<StudentPoints> {
    @Insert("insert into student_points (username, classID, points) VALUES (#{username},#{classID},#{points})")
    public void newStu(StudentPoints studentPoints);

    @Select("select 1 from student_points where username=#{username}")
    public boolean existedStu(String username);

    @Update("update student_points set points=points+#{points} where username=#{username}")
    public void addPoints(StudentPoints studentPoints);

    @Select("select * from student_points where username=#{username}")
    StudentPoints stuPoints(String username);

    @Select("select s.*,u.realName from student_points s join user u on u.username = s.username where classID=#{classID}")
    @Results(id = "StuPointsWithRealName_Map", value = {
            @Result(property = "realName", column = "realName"),
    })
    List<StuPointsWithRealName> stusPoints(String classID);

    @Update("update student_points set points=60 where classID=#{classID}")
    void remakePoints(String classID);

}




