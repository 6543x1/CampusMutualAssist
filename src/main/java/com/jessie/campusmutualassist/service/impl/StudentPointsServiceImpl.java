package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jessie.campusmutualassist.aop.PointsOperationLog;
import com.jessie.campusmutualassist.entity.StuPointsWithRealName;
import com.jessie.campusmutualassist.entity.StudentPoints;
import com.jessie.campusmutualassist.mapper.StudentPointsMapper;
import com.jessie.campusmutualassist.service.StudentPointsService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Service("studentPointsService")
public class StudentPointsServiceImpl extends ServiceImpl<StudentPointsMapper, StudentPoints>
        implements StudentPointsService {
    @Autowired
    StudentPointsMapper studentPointsMapper;
    @Autowired
    private RedissonClient redissonClient;

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
        RLock lock = redissonClient.getLock("PointsLock:" + studentPoints.getUsername());
        try {
            if (lock.tryLock(1, 3, TimeUnit.SECONDS)) {//第一个：等待获取锁的时间 第二个：超时释放时间
                //考虑到一个班级里同时操作加分的可能性不太高（？），所以等待时间并没有设置的很长
                studentPointsMapper.addPoints(studentPoints);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Async
    @Override
    @PointsOperationLog(module = "学生加法", type = "common", desc = "加分")
    public void addStusPoints(Set<String> students, String classID, int points, String reason,String operator) {
        students.forEach((stu)->{
            addPoints(new StudentPoints(stu, classID, points));
        });
    }


    @Override
    public StudentPoints StuPoints(String username) {
        return studentPointsMapper.stuPoints(username);
    }

    @Override
    public List<StuPointsWithRealName> StusPoints(String classID) {
        return studentPointsMapper.stusPoints(classID);
    }

    @Override
    public void remakePoints(String classID) {
        studentPointsMapper.remakePoints(classID);
    }
}




