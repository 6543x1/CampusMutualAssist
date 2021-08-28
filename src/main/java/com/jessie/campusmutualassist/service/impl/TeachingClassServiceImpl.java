package com.jessie.campusmutualassist.service.impl;

import com.jessie.campusmutualassist.entity.SimpleStu;
import com.jessie.campusmutualassist.entity.TeachingClass;
import com.jessie.campusmutualassist.mapper.TeachingClassMapper;
import com.jessie.campusmutualassist.service.TeachingClassService;
import com.jessie.campusmutualassist.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("teachingClassService")
public class TeachingClassServiceImpl implements TeachingClassService {

    @Autowired
    TeachingClassMapper teachingClassMapper;
    @Autowired
    RedisUtil redisUtil;
    @Override
    public void createClass(TeachingClass teachingClass) {
        teachingClassMapper.createClass(teachingClass);
    }

    @Override
    public List<TeachingClass> getCreatedClass(String teacher) {
        return teachingClassMapper.getCreatedClass(teacher);
    }

    @Override
    public void transferTeacher(String newTeacher) {
        teachingClassMapper.transferTeacher(newTeacher);
    }

    @Override
    public List<SimpleStu> getSimpleStuList(String classID) {
        return teachingClassMapper.getSimpleStuList(classID);
    }

    @Override
    public void deleteClass(String classID){
        Set<String> keys = redisUtil.keys("class:" + classID + "*");
        redisUtil.delete(keys);
        teachingClassMapper.deleteClass(classID);
    }
}
