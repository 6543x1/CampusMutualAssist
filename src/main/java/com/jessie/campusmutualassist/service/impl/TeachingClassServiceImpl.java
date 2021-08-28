package com.jessie.campusmutualassist.service.impl;

import com.jessie.campusmutualassist.entity.SimpleStu;
import com.jessie.campusmutualassist.entity.TeachingClass;
import com.jessie.campusmutualassist.mapper.TeachingClassMapper;
import com.jessie.campusmutualassist.service.TeachingClassService;
import com.jessie.campusmutualassist.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(value = "createdClass",key = "#teacher+'*'")
    public void createClass(TeachingClass teachingClass) {
        teachingClassMapper.createClass(teachingClass);
    }

    @Override
    @Cacheable(value = "createdClass",key = "#teacher")
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
