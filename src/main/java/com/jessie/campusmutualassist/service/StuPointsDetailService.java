package com.jessie.campusmutualassist.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jessie.campusmutualassist.entity.StuPointsDetail;

import java.util.List;
import java.util.Set;

/**
 *
 */
public interface StuPointsDetailService extends IService<StuPointsDetail> {
    void newDetail(StuPointsDetail stuPointsDetail, Set<String> students);

    List<StuPointsDetail> classDetails(String classID);

    List<StuPointsDetail> stuClassDetails(String classID, String target);

    void deleteOldItems(String classID);
}
