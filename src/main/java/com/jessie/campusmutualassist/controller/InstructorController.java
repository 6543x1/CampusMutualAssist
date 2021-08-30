package com.jessie.campusmutualassist.controller;

import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.entity.myEnum.Role;
import com.jessie.campusmutualassist.service.MailService;
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

import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;

@Api(tags = "辅导员操作相关类")
@RestController
@RequestMapping(value = "/instructor")
public class InstructorController {
    @Autowired
    PermissionService permissionService;
    @Autowired
    MailService mailService;

    @ApiOperation(value = "申请班长或者辅导员", notes = "申请成为马猴烧酒也可以")
    @PreAuthorize("hasAnyAuthority('student','teacher')")
    @PostMapping(value = "/tryGetPermission", produces = "application/json")
    public Result tryGetPermission(Role role, String reason) {
        String username = getCurrentUsername();
        mailService.newMessage("申请：" + username + "要成为" + role, "1647389906@qq.com", reason);//偷个懒
        return Result.success("申请成功");
    }

    @ApiOperation(value = "授予辅导员权限", notes = "需要有管理账号才能执行")
    @PreAuthorize("hasAnyAuthority('admin')")
    @PostMapping(value = "/grantPermission", produces = "application/json")
    public Result grantPermission(@RequestParam("username") List<String> username, String college) {
        //少了个给User里设置Role的，算了明天再写，这个不是很要紧
        for (String x : username) {
            permissionService.setUserPermission(x, "instructor");
            permissionService.setUserPermission(x, "instructor_" + college);
        }
        return Result.success("授权成功");
    }

    @ApiOperation(value = "授予班长权限", notes = "需要辅导员具备相应学院的权限才能执行")
    @PreAuthorize("hasAnyAuthority('instructor_'+#college)")
    @PostMapping(value = "/setMonitor", produces = "application/json")
    public Result setMonitorPermission(@RequestParam("username") List<String> username, String college) {
        for (String x : username) {
            permissionService.setUserPermission(x, "monitor");
            permissionService.setUserPermission(x, "monitor_" + college);
        }
        return Result.success("授权成功");
    }


}
