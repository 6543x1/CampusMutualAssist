package com.jessie.campusmutualassist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jessie.campusmutualassist.entity.StudentPoints;
import com.jessie.campusmutualassist.entity.StuPointsWithRealName;
import java.util.List;
import java.util.Set;

/**
 *
 */
public interface StudentPointsService extends IService<StudentPoints> {
    void newStu(StudentPoints studentPoints);
    boolean existedStu(String username);
    void addPoints(StudentPoints studentPoints);
    void addStusPoints(Set<String> stuList,String classID,int points);
    StudentPoints StuPoints(String username);
    List<StuPointsWithRealName> StusPoints(String classID);
}
