package com.jessie.campusmutualassist.service;

import com.jessie.campusmutualassist.entity.TeachingClass;

import java.util.List;

public interface TeachingClassService {

    public void createClass(TeachingClass teachingClass);
    public List<TeachingClass> getCreatedClass(String teacher);
}