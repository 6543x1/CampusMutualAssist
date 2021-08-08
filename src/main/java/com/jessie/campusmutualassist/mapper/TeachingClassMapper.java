package com.jessie.campusmutualassist.mapper;

import com.jessie.campusmutualassist.entity.TeachingClass;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TeachingClassMapper {

    @Insert("insert into teaching_class (name, schedule, teacher, college, id) VALUES (#{name},#{schedule},#{teacher},#{college},#{id})")
    void createClass(TeachingClass teachingClass);
    @Select("select * from teaching_class where teacher=#{teacher}")
    List<TeachingClass> getCreatedClass(String teacher);
}
