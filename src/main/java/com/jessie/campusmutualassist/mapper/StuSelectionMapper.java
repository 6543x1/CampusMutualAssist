package com.jessie.campusmutualassist.mapper;

import com.jessie.campusmutualassist.entity.StuSelection;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StuSelectionMapper {
    @Select("select * from stu_selection where stuName=#{stuName}")
    List<StuSelection> getStuSelections(String stuName);
    @Insert("insert into stu_selection (stuName, classID, scores) VALUES (#{stuName},#{classID},#{scores})")
    void newSelections(StuSelection stuSelection);
    @Select("select * from stu_selection where classID=#{classID}")
    List<StuSelection> getClassSelections(String classID);
    @Select("select stuName from stu_selection where classID=#{classID}")
    List<String> getClassSelectStuName(String classID);

}
