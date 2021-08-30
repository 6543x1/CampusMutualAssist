package com.jessie.campusmutualassist.service.impl;

import com.jessie.campusmutualassist.mapper.AdminOperationMapper;
import com.jessie.campusmutualassist.entity.AdminOperation;
import com.jessie.campusmutualassist.service.AdminOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("adminOperationService")
public class AdminOperationServiceImpl implements AdminOperationService {
    @Autowired
    AdminOperationMapper adminOperationMapper;

    @Override
    public void newOperation(AdminOperation adminOperation) {
        adminOperationMapper.newOperation(adminOperation);
    }

    @Override
    public List<AdminOperation> getAllOperations(int targetUser) {
        return null;
    }

    @Override
    public List<AdminOperation> getAnAdminOperations(int operator) {
        return adminOperationMapper.getAnAdminOperations(operator);
    }

    @Override
    public List<AdminOperation> getOperationsByType(String operation) {
        return adminOperationMapper.getOperationsByType(operation);
    }
}
