package com.jessie.campusmutualassist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jessie.campusmutualassist.entity.StudentPoints;
import com.jessie.campusmutualassist.entity.StuPointsWithRealName;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Set;

/**
 *
 */
public interface StudentPointsService extends IService<StudentPoints> {
    void newStu(StudentPoints studentPoints);

    boolean existedStu(String username);

    void addPoints(StudentPoints studentPoints);

    @Async
    void addStusPoints(Set<String> students, String classID, int points, String reason,String operator);

    StudentPoints StuPoints(String username);

    List<StuPointsWithRealName> StusPoints(String classID);

    void remakePoints(String classID);
}
