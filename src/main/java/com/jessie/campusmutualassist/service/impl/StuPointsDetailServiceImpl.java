package com.jessie.campusmutualassist.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jessie.campusmutualassist.entity.StuPointsDetail;
import com.jessie.campusmutualassist.mapper.StuPointsDetailMapper;
import com.jessie.campusmutualassist.service.StuPointsDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 *
 */
@Service
public class StuPointsDetailServiceImpl extends ServiceImpl<StuPointsDetailMapper, StuPointsDetail>
        implements StuPointsDetailService {
    @Autowired
    StuPointsDetailMapper stuPointsDetailMapper;

    @Override
    @Async
    public void newDetail(StuPointsDetail stuPointsDetail, Set<String> students) {
        for (String x : students) {
            stuPointsDetail.setTarget(x);
            stuPointsDetailMapper.newDetail(stuPointsDetail);
        }
    }

    @Override
    public List<StuPointsDetail> classDetails(String classID) {
        return stuPointsDetailMapper.classDetails(classID);
    }

    @Override
    public List<StuPointsDetail> stuClassDetails(String classID, String target) {
        return stuPointsDetailMapper.stuClassDetails(classID, target);
    }

    @Override
    public void deleteOldItems(String classID) {
        stuPointsDetailMapper.deleteOldItems(classID);
    }
}




