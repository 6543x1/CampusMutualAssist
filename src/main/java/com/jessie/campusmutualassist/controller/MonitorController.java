package com.jessie.campusmutualassist.controller;


import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.entity.TeachingClass;
import com.jessie.campusmutualassist.service.PermissionService;
import com.jessie.campusmutualassist.service.TeachingClassService;
import com.jessie.campusmutualassist.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.jessie.campusmutualassist.service.impl.MailServiceImpl.getRandomString;
import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;

@Api(tags = "班长相关操作")
@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    PermissionService permissionService;
    @Autowired
    TeachingClassService teachingClassService;
    @Autowired
    RedisUtil redisUtil;


    @ApiOperation(value = "班长创建班级")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "班级名字", dataType = "String"),
            @ApiImplicitParam(name = "schedule", value = "班级上课时间地点", dataType = "String")
    })
    @PreAuthorize("hasAnyAuthority('monitor_'+#teachingClass.college)")
    @PostMapping(value = "/createClass", produces = "application/json;charset=UTF-8")
    public Result createClass(TeachingClass teachingClass, HttpServletRequest request) {
        try {
            String username = getCurrentUsername();
            teachingClass.setTeacher(username);
            teachingClass.setId(getRandomString());
            teachingClassService.createClass(teachingClass);
            permissionService.setUserPermission(username, "teacher_" + teachingClass.getId());
            permissionService.setUserPermission(username, "monitor_" + teachingClass.getId());//这个权限主要是可以移交
            //其实我也不知道给老师移交有啥用，所以就只给班长移交了
            permissionService.setUserPermission(username, "student_" + teachingClass.getId());//注意！班长自己也是学生
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建失败，请检查是否有误后重试");
        }
        redisUtil.sAdd("class:" + teachingClass.getId() + "type:" + "members", getCurrentUsername());//一定要把自己加入！
        return Result.success("班级创建成功", teachingClass.getId());
    }

    @ApiOperation(value = "转移班长权限", notes = "需要班长才能执行")
    @PreAuthorize("hasAuthority ('monitor_'+#classID) AND hasAuthority('monitor')")
    @PostMapping(value = "/{classID}/transfer", produces = "application/json;charset=UTF-8")
    public Result transferMonitorPermission(@PathVariable("classID") String classID, String newMonitor) {
        //此处应当设置一个安全检查
        permissionService.setUserPermission(newMonitor, "monitor");
        permissionService.setUserPermission(newMonitor, "monitor_" + classID);
        permissionService.setUserPermission(newMonitor, "teacher_" + classID);
        permissionService.deleteUserPermission(newMonitor, "teacher_" + classID);
        permissionService.deleteUserPermission(getCurrentUsername(), "monitor_classID");
        permissionService.deleteUserPermission(getCurrentUsername(), "monitor");
        teachingClassService.transferTeacher(newMonitor);
        return Result.success("移交成功");
    }


}
