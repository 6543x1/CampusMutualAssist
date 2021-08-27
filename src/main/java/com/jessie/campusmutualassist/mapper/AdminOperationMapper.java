package com.jessie.campusmutualassist.mapper;

import com.jessie.campusmutualassist.entity.AdminOperation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface AdminOperationMapper
{
    @Insert("insert into admin_operation (operator, operation, targetUser, targetData,operatedTime,reason) VALUES (#{operator},#{operation},#{targetUser},#{targetData},#{operatedTime},#{reason})")
    void newOperation(AdminOperation adminOperation);

    @Select("select * from admin_operation where targetUser=#{targetUser}")
    List<AdminOperation> getAllOperations(int targetUser);

    @Select("select * from admin_operation where operator=#{operator}")
    List<AdminOperation> getAnAdminOperations(int operator);

    @Select("select * from admin_operation where operation=#{operation};")
    List<AdminOperation> getOperationsByType(String operation);
    @Select("select * from  admin_operation")
    List<AdminOperation> findAll();
}
