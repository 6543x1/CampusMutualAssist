package com.jessie.campusmutualassist.controller;

import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.entity.StuSelection;
import com.jessie.campusmutualassist.entity.StudentPoints;
import com.jessie.campusmutualassist.entity.Vote;
import com.jessie.campusmutualassist.service.*;
import com.jessie.campusmutualassist.utils.JwtTokenUtil;
import com.jessie.campusmutualassist.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;
@Api(tags = "学生通用操作类")
@RestController
@RequestMapping("/classes")
public class StuController {
    @Autowired
    TeachingClassService teachingClassService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    NoticeService noticeService;
    @Autowired
    StuSelectionService stuSelectionService;
    @Autowired
    VoteService voteService;
    @Autowired
    SignInService signInService;
    @Autowired
    StudentPointsService studentPointsService;
    @ApiOperation(value = "加入班级",notes = "老师生成班级后会获得一个ID，请妥善保存")
    @PreAuthorize("hasAnyAuthority('student')")
    @PostMapping(value = "/{classID}/join",produces = "application/json;charset=UTF-8")
    public Result joinClass(@PathVariable("classID") String classID, HttpServletRequest request){
        if(redisUtil.hasKey("classID"+":"+classID+":type:"+"Auto_AcceptStu")){
            redisUtil.sAdd("class:"+classID+":"+"type:"+"members",getCurrentUsername());
            return Result.success("已经成功加入");
        }
        else {
            redisUtil.sAdd("class:" + classID + ":" + "type:" + "JoinQueue", getCurrentUsername());
        }
        StudentPoints studentPoints=new StudentPoints();
        studentPoints.setPoints(0);
        studentPoints.setClassID(classID);
        studentPoints.setUsername(getCurrentUsername());
        studentPointsService.newStu(studentPoints);
        return Result.success("已经成功申请加入该班级，请等待同意");
    }

    @ApiOperation(value = "查询当前账号所加入的全部班级",
            notes = "老师目前不算在里面"
    )
    @PreAuthorize("hasAnyAuthority('student')")
    @GetMapping(value = "/getMyClass",produces = "application/json;charset=UTF-8")
    public Result getClassInfo()
    {
        List<StuSelection> stuSelections=stuSelectionService.getStuSelections(getCurrentUsername());
        return Result.success("查询学生信息成功",stuSelections);
    }
    @ApiOperation(value = "学生投票，多选投票在selections中加上多个选项即可")
    @PreAuthorize("hasAnyAuthority('student_'+#classID)")//和下面保持一致
    @PostMapping(value = "/{classID}/vote", produces = "application/json;charset=UTF-8")
    public Result vote(@PathVariable("classID") String classID, long vid,@RequestParam("selections") Set<String> selections) {
        if(redisUtil.sIsMember("class:" + classID + ":" + "type:" + "Voter"+":"+"vid:"+vid,getCurrentUsername())){
            return Result.error("你已投票，请勿重复投票");
        }
        Vote vote=voteService.getVote(vid);
        if(vote.getDeadLine().isBefore(LocalDateTime.now())){
            return Result.error("投票已经过期!");
        }
        if(vote.getLimitation()<selections.size()){
            return Result.error("最多只能选择"+vote.getLimitation()+"项选项！");
        }
        for(String x:selections){
            redisUtil.zIncrementScore("class:" + classID + ":" + "type:" + "VoteSelections"+":"+"vid:"+vid,x,1);
        }
        redisUtil.sAdd("class:" + classID + ":" + "type:" + "Voter"+":"+"vid"+vid,getCurrentUsername());
        if(!vote.isAnonymous()){
            for(String x:selections){
            redisUtil.sAdd("class:" + classID + ":" + "type:" + "Voter"+":"+"vid:"+vid+":Selection:"+x,getCurrentUsername());
            }
        }
        //请勿添加过多选项，以免响应缓慢
        return Result.success("投票成功");
    }
    //我觉得投票的结果还是要存到数据库里去，可以用消息队列来解决这个问题，要不就@Async

    @ApiOperation(value = "签到，直接以signIn来请求")
    @PreAuthorize("hasAnyAuthority('student_'+#classID)")//和下面保持一致
    @PostMapping(value = "/{classID}/signIn", produces = "application/json;charset=UTF-8")
    public Result SignIn(@PathVariable("classID") String classID, long signID,String key)
    {
        if(!signInService.getSignIn(signID).getSignKey().equals(key)){
            return Result.error("key不对");
        }
       if(redisUtil.hasKey("class:" + classID + ":type:" + "signIn"+":"+"signID:"+signID)){
           redisUtil.sRemove("class:" + classID + ":type:" + "signIn"+":"+"signID:"+signID,getCurrentUsername());
       }
       else{
           return Result.error("签到不存在或已过期！",404);
       }
       return Result.success("签到成功");
    }
    @ApiOperation(value = "确认公告")
    @PreAuthorize("hasAnyAuthority('student_'+#classID)")//和下面保持一致
    @PostMapping(value = "/{classID}/notice/confirm", produces = "application/json;charset=UTF-8")
    public Result confirmNotice(@PathVariable("classID") String classID, long nid) {
        if(!noticeService.getNoticeDeadLine(nid)){
            return Result.error("已经超过了公告的确认时间！");
        }
        redisUtil.sAdd("class:" + classID + ":type:" + "noticeConfirmed"+":"+"nid:"+nid,getCurrentUsername());
        return Result.success("已确认");
    }


}
