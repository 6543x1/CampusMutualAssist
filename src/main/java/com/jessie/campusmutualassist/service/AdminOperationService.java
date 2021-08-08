package com.jessie.campusmutualassist.service;



import com.jessie.campusmutualassist.entity.AdminOperation;

import java.util.List;

public interface AdminOperationService
{
    void newOperation(AdminOperation adminOperation);

    List<AdminOperation> getAllOperations(int targetUser);

    List<AdminOperation> getAnAdminOperations(int operator);

    List<AdminOperation> getOperationsByType(String operation);
}
