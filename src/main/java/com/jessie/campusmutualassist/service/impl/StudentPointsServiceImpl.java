package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jessie.campusmutualassist.entity.StudentPoints;
import com.jessie.campusmutualassist.service.StudentPointsService;
import com.jessie.campusmutualassist.mapper.StudentPointsMapper;
import com.jessie.campusmutualassist.entity.StuPointsWithRealName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 *
 */
@Service("studentPointsService")
public class StudentPointsServiceImpl extends ServiceImpl<StudentPointsMapper, StudentPoints>
    implements StudentPointsService{
    @Autowired
    StudentPointsMapper studentPointsMapper;
    @Override
    public void newStu(StudentPoints studentPoints) {
        studentPointsMapper.newStu(studentPoints);
    }

    @Override
    public boolean existedStu(String username) {
        return studentPointsMapper.existedStu(username);
    }

    @Override
    public void addPoints(StudentPoints studentPoints) {
        studentPointsMapper.addPoints(studentPoints);
    }
    @Async
    @Override
    public void addStusPoints(Set<String> stuList, String classID, int points) {
        for(String stu:stuList){
            addPoints(new StudentPoints(stu,classID,points));
        }
    }

    @Override
    public StudentPoints StuPoints(String username) {
        return studentPointsMapper.stuPoints(username);
    }

    @Override
    public List<StuPointsWithRealName> StusPoints(String classID) {
        return studentPointsMapper.stusPoints(classID);
    }

}




