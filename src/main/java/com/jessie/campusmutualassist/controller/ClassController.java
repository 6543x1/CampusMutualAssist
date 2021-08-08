package com.jessie.campusmutualassist.controller;

import com.jessie.campusmutualassist.entity.Notice;
import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.entity.StuSelection;
import com.jessie.campusmutualassist.service.NoticeService;
import com.jessie.campusmutualassist.service.StuSelectionService;
import com.jessie.campusmutualassist.service.UserService;
import com.jessie.campusmutualassist.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;
@Api(tags = "班级通用操作")
@RestController
@RequestMapping("/classes/{classID}")
public class ClassController {

    @Autowired
    StuSelectionService stuSelectionService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserService userService;
    @Autowired
    NoticeService noticeService;

    @ApiOperation(value = "查询当前账号所加入的全部班级",
            notes = "老师目前不算在里面"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="classID",value = "班级ID",required = true,paramType = "path")
    }
    )
    @PreAuthorize("hasAnyAuthority('stu_'+#classID)")
    @GetMapping(value = "/getMyClass",produces = "application/json;charset=UTF-8")
    public Result getClassInfo(@PathVariable("classID")String classID)
    {
        List<StuSelection> stuSelections=stuSelectionService.getStuSelections(getCurrentUsername());
        return Result.success("查询学生信息成功",stuSelections);
    }

    @ApiOperation(value = "查询当前班级的全部学生及其对应真名",
            notes = "建议前端保存名字和username映射关系到本地，节约服务器宝贵资源（）")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/getStuList",produces = "application/json;charset=UTF-8")
    public Result getStuList(@PathVariable("classID")String classID)
    {
        Set<String> mySet=redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "members");
        List<Map<String, String>> realNameWithUsername = userService.getRealNameWithUsername(mySet);
        return Result.success("查询已加入学生成功(附带名字)",realNameWithUsername);
    }
    @ApiOperation(value = "查看班级公告")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/{classID}/notice",produces = "application/json;charset=UTF-8")
    public Result receiveAllNotices(@PathVariable("classID") String classID){
        List<Notice> noticeList=noticeService.getClassNotices(classID);
        return Result.success("获取公告成功",noticeList);
    }
    @ApiOperation(value = "获取已确认公告的学生名单")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/notice/noticeConfirmedStu", produces = "application/json;charset=UTF-8")
    public Result noticeUnConfirmed(@PathVariable("classID") String classID,long nid) {
        Set<String> memSet=redisUtil.sGetMembers("class:" + classID + ":type:" + "noticeConfirmed"+":"+"nid:"+nid);
        return Result.success("已获取确认名单",memSet);
    }
    @ApiOperation(value = "获取当前班级的最新随机选人结果")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/RandomStu", produces = "application/json;charset=UTF-8")
    public Result getRandomStu(@PathVariable("classID") String classID) {
        Set<String> memSet=redisUtil.sGetMembers("class:" + classID +":"+"type:" + "RandomSelect");
        return Result.success("已获取随机选人结果",memSet);
    }

}
