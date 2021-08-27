package com.jessie.campusmutualassist.mapper;

import com.jessie.campusmutualassist.entity.SimpleStu;
import com.jessie.campusmutualassist.entity.TeachingClass;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface TeachingClassMapper {

    @Insert("insert into teaching_class (name, schedule, teacher, college, id) VALUES (#{name},#{schedule},#{teacher},#{college},#{id})")
    void createClass(TeachingClass teachingClass);
    @Select("select * from teaching_class where teacher=#{teacher}")
    List<TeachingClass> getCreatedClass(String teacher);
    @Update("update teaching_class set teacher=#{newTeacher}")
    void transferTeacher(String newTeacher);

    List<SimpleStu> getSimpleStuList(String classID);
}
