package com.jessie.campusmutualassist.service.impl;

import com.jessie.campusmutualassist.entity.StuSelection;
import com.jessie.campusmutualassist.mapper.StuSelectionMapper;
import com.jessie.campusmutualassist.service.StuSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("stuSelectionService")
public class StuSelectionServiceImpl implements StuSelectionService {
    @Autowired
    StuSelectionMapper stuSelectionMapper;
    @Override
    public List<StuSelection> getStuSelections(String stuName) {
        return stuSelectionMapper.getStuSelections(stuName);
    }

    @Override
    @Async
    public void newSelections(StuSelection stuSelection) {
        stuSelectionMapper.newSelections(stuSelection);
    }

    @Override
    public List<StuSelection> getClassSelections(String className) {
        return stuSelectionMapper.getClassSelections(className);
    }
}
