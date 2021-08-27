package com.jessie.campusmutualassist.controller;

import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags= "辅导员操作相关类")
@RestController
@RequestMapping(value = "/instructor")
public class InstructorController {
    @Autowired
    PermissionService permissionService;

    @ApiOperation(value = "授予辅导员权限",notes = "需要有管理账号才能执行")
    @PreAuthorize("hasAnyAuthority('admin')")
    @PostMapping(value = "/grantPermission",produces = "application/json")
    public Result grantPermission(@RequestParam("username") List<String> username,String college){
        //少了个给User里设置Role的，算了明天再写，这个不是很要紧
        for(String x:username){
            permissionService.setUserPermission(x,"instructor");
            permissionService.setUserPermission(x,"instructor_"+college);
        }
        return Result.success("授权成功");
    }
    @ApiOperation(value = "授予班长权限",notes = "需要辅导员具备相应学院的权限才能执行")
    @PreAuthorize("hasAnyAuthority('instructor_'+#college)")
    @PostMapping(value = "/setMonitor",produces = "application/json")
    public Result setMonitorPermission(@RequestParam("username") List<String> username,String college){
        for(String x:username){
            permissionService.setUserPermission(x,"monitor");
            permissionService.setUserPermission(x,"monitor_"+college);
        }
        return Result.success("授权成功");
    }




}
