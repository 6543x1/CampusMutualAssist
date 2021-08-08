package com.jessie.campusmutualassist.service.impl;

import com.jessie.campusmutualassist.entity.TeachingClass;
import com.jessie.campusmutualassist.mapper.TeachingClassMapper;
import com.jessie.campusmutualassist.service.TeachingClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("teachingClassService")
public class TeaingClassServiceImpl implements TeachingClassService {

    @Autowired
    TeachingClassMapper teachingClassMapper;
    @Override
    public void createClass(TeachingClass teachingClass) {
        teachingClassMapper.createClass(teachingClass);
    }

    @Override
    public List<TeachingClass> getCreatedClass(String teacher) {
        return teachingClassMapper.getCreatedClass(teacher);
    }
}
