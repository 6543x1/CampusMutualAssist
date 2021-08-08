package com.jessie.campusmutualassist.service;

import com.jessie.campusmutualassist.entity.StuSelection;

import java.util.List;

public interface StuSelectionService {
    List<StuSelection> getStuSelections(String stuName);
    void newSelections(StuSelection stuSelection);
    List<StuSelection> getClassSelections(String className);
}
