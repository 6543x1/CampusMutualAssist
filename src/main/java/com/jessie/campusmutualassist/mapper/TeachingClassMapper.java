package com.jessie.campusmutualassist.mapper;

import com.jessie.campusmutualassist.entity.SimpleStu;
import com.jessie.campusmutualassist.entity.TeachingClass;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface TeachingClassMapper {

    @Insert("insert into teaching_class (name, teacher, id) VALUES (#{name},#{teacher},#{id})")
    void createClass(TeachingClass teachingClass);

    @Select("select * from teaching_class where teacher=#{teacher}")
    List<TeachingClass> getCreatedClass(String teacher);

    @Update("update teaching_class set teacher=#{newTeacher}")
    void transferTeacher(String newTeacher);

    @Delete("delete from teaching_class where id=#{classID}")
    void deleteClass(String classID);

    List<SimpleStu> getSimpleStuList(String classID);
}
