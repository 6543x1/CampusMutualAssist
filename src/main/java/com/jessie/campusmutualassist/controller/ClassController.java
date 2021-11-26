package com.jessie.campusmutualassist.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.*;
import com.jessie.campusmutualassist.exception.NoAccessException;
import com.jessie.campusmutualassist.service.*;
import com.jessie.campusmutualassist.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;

@Api(tags = "班级通用操作")
@RestController
@RequestMapping("/classes/{classID}")
@Slf4j
public class ClassController {

    @Autowired
    StuSelectionService stuSelectionService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserService userService;
    @Autowired
    NoticeService noticeService;
    @Autowired
    StudentPointsService studentPointsService;
    @Autowired
    FilesService filesService;
    @Autowired
    SignInService signInService;
    @Autowired
    VoteService voteService;
    @Autowired
    TeachingClassService teachingClassService;
    @Autowired
    PermissionService permissionService;
    //引出了一个更深的问题，怎么把助教＆学生名单一起打包返回？
    //那么，必定需要，封装一个新的类来进行返回了


    @ApiOperation(value = "查询当前班级的全部学生及其对应真名",
            notes = "建议前端保存名字和username映射关系到本地，节约服务器宝贵资源（）")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/getStuList", produces = "application/json;charset=UTF-8")
    //获取分数！！！！建议改一下链接
    public List getStuScores(@PathVariable("classID") String classID) {

//        Set<String> mySet;
//        if(redisUtil.hasKey("class:" + classID + ":" + "type:" + "members")){
//        mySet=redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "members");
//        }
//        else{
//            mySet=new HashSet<>(stuSelectionService.getClassSelectStuName(classID));
//            redisUtil.sAdd("class:" + classID + ":" + "type:" + "members",mySet.toArray(new String[0]));
//        }
//        List<Map<String, String>> realNameWithUsername = userService.getRealNameWithUsername(mySet);
        //查询时，应该带上老师
        //同时附带上积分信息.......
        List<SimpleStu> list = teachingClassService.getSimpleStuList(classID);
        return list;
    }

    @ApiOperation(value = "分类查询当前班级的全部成员",
            notes = "包括老师班长管理员学生")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/ClassMembers", produces = "application/json;charset=UTF-8")
    public JSONObject getClassMembers(@PathVariable("classID") String classID) {
        return permissionService.getClassListSortByPermission(classID);
    }

    @ApiOperation(value = "查看班级公告")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/notice", produces = "application/json;charset=UTF-8")
    public Result receiveAllNotices(@PathVariable("classID") String classID) {
        List<Notice> noticeList = noticeService.getClassPublicNotices(classID);
        return Result.success("获取公告成功", noticeList);
    }

    @ApiOperation(value = "查看班级公告（包含文件）")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/noticesWithFiles", produces = "application/json;charset=UTF-8")
    public Result receiveAllPublicNoticesWithFiles(@PathVariable("classID") String classID) {
        List<NoticeWithFiles> noticeList = noticeService.getPublicNoticesWithFiles(classID);
        return Result.success("获取公告成功", noticeList);
    }

    @ApiOperation(value = "单独查看某个班级的某一个公告（包含文件）")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/noticeWithFiles", produces = "application/json;charset=UTF-8")
    public Result aPublicNoticesWithFiles(@PathVariable("classID") String classID, long nid) {
        NoticeWithFiles notice = null;
        try {
            notice = noticeService.getNoticeWithFile(nid, getCurrentUsername());
        } catch (NoAccessException e) {
            return Result.error("你无权限查看此公告！");
        }
        return Result.success("获取单个公告成功", notice);
    }


    @ApiOperation(value = "查看非公开公告（部分人可见）")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/unPublicNotice", produces = "application/json;charset=UTF-8")
    public Result receiveAllUnPublicNotices(@PathVariable("classID") String classID) {
        List<Notice> noticeList = noticeService.getClassUnPublicNotices(classID, getCurrentUsername());
        return Result.success("获取非公开公告成功", noticeList);
    }

    @ApiOperation(value = "查看班级公告(以分页形式)")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/noticePage", produces = "application/json;charset=UTF-8")
    public PageInfo receiveAllNoticesByPage(@PathVariable("classID") String classID, @RequestParam(defaultValue = "1") int pageNum) {
        PageInfo pageInfo = noticeService.getClassNoticesPage(classID, pageNum);
        return pageInfo;
    }

    @ApiOperation(value = "获取还未参与的公告投票签到")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    //参与英文忘了...
    @GetMapping(value = "/unconfirmed", produces = "application/json;charset=UTF-8")
    public Result noticeUnConfirmed(@PathVariable("classID") String classID) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("notice", noticeService.getUnConfirmedNotices(classID, getCurrentUsername()));
        jsonObject.put("vote", voteService.getNotVotes(getCurrentUsername(), classID));
        jsonObject.put("signIn", signInService.getMyNotSign(classID, getCurrentUsername()));
        return Result.success("已获取未参与的信息", jsonObject);
    }

    @ApiOperation(value = "获取已确认公告的学生名单")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/notice/noticeConfirmedStu", produces = "application/json;charset=UTF-8")
    public Result noticeUnConfirmed(@PathVariable("classID") String classID, long nid) {
        Set<String> memSet = redisUtil.sGetMembers("class:" + classID + ":type:" + "noticeConfirmed" + ":" + "nid:" + nid);
        Set<String> notConfirmed = redisUtil.sDifference("class:" + classID + ":" + "type:" + "members", "class:" + classID + ":type:" + "noticeConfirmed" + ":" + "nid:" + nid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("confirmed", memSet);
        jsonObject.put("unconfirmed", notConfirmed);
        return Result.success("已获取确认名单", jsonObject);
    }

    @ApiOperation(value = "获取当前班级的最新随机选人结果")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/RandomStu", produces = "application/json;charset=UTF-8")
    public Result getRandomStu(@PathVariable("classID") String classID) {
        Set<String> memSet = redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "RandomSelect");
        String[] info = redisUtil.get("class:" + classID + ":" + "type:" + "RandomSelectInfo").split(":");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("publishedTime", LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(info[0])), ZoneId.systemDefault()));
        jsonObject.put("publisher", info[1]);
        jsonObject.put("students", memSet);
        return Result.success("获取成功", jsonObject);
    }


    @ApiOperation(value = "获取当前班级的最新课堂！随机选人结果")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/CourseRandomStu", produces = "application/json;charset=UTF-8")
    public Result getCourseRandomStu(@PathVariable("classID") String classID) {
        Map<Object, Object> objectObjectMap = redisUtil.hGetAll("class:" + classID + ":" + "type:" + "CourseRandomSelect");
        String[] info = redisUtil.get("class:" + classID + ":" + "type:" + "CourseRandomSelectInfo").split(":");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("publishedTime", LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(info[0])), ZoneId.systemDefault()));
        jsonObject.put("publisher", info[1]);
        jsonObject.put("students", objectObjectMap);
        return Result.success("已获取随机选人结果", jsonObject);//别问，问就是IDE自动生成名字懒得改，虽然这个名字怪怪的
    }

    @ApiOperation(value = "获取签到情况")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID,'student_'+#classID)")
    @PostMapping(value = "/getSignInDetail", produces = "application/json;charset=UTF-8")
    public Result checkSignIn(@PathVariable("classID") String classID, String signID) {
        Set<String> notSingInList = redisUtil.sGetMembers("class:" + classID + ":type:" + "signIn" + ":" + "signId:" + signID);
        Set<String> signInList = redisUtil.sDifference("class:" + classID + ":" + "type:" + "members", "class:" + classID + ":type:" + "signIn" + ":" + "signId:" + signID);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("signed", signInList);
        jsonObject.put("unSigned", notSingInList);
        return Result.success("签到的学生/未签到情况", jsonObject);
    }

    @ApiOperation(value = "查询是否自动同意学生加入")
    @PreAuthorize("hasAnyAuthority('teacher','student')")
    @GetMapping(value = "/autoAccept", produces = "application/json;charset=UTF-8")
    public Result autoAcceptStudentJoin(@PathVariable("classID") String classID) {
        return Result.success(redisUtil.hasKey("classID" + ":" + classID + ":type:" + "Auto_AcceptStu").toString());
    }

    @ApiOperation(value = "查询已经投票的人")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID,'student_'+#classID)")
    @GetMapping(value = "/voter", produces = "application/json;charset=UTF-8")
    public Result getVoter(@PathVariable("classID") String classID, String vid) {
        Set<String> voteList = redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "Voter" + ":" + "vid" + vid);
        Set<String> notVoteList = redisUtil.sDifference("class:" + classID + ":" + "type:" + "members", "class:" + classID + ":" + "type:" + "Voter" + ":" + "vid" + vid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("voted", voteList);
        jsonObject.put("notVoted", notVoteList);
        return Result.success("查询成功", jsonObject);
    }

    @ApiOperation(value = "查询投票每个人对应选项选的啥")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID,'student_'+#classID)")
    @GetMapping(value = "/voterSelections", produces = "application/json;charset=UTF-8")
    public Result getVoterResult(@PathVariable("classID") String classID, String vid) {
        Set<String> selections = redisUtil.zRange("class:" + classID + ":" + "type:" + "VoteSelections" + ":" + "vid:" + vid, 0, -1);
        HashMap<String, Set<String>> selectorMap = new HashMap<>();
        for (String selection : selections) {
            if (redisUtil.hasKey("class:" + classID + ":" + "type:" + "Voter" + ":" + "vid:" + vid + ":Selection:" + selection)) {
                selectorMap.put(selection, redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "Voter" + ":" + "vid:" + vid + ":Selection:" + selection));
            } else {
                selectorMap.put(selection, null);
            }
        }
        return Result.success("查询成功", selectorMap);
    }

    @ApiOperation(value = "单文件直接获取")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID,'student_'+#classID)")
    @GetMapping(value = "/file", produces = "application/json;charset=UTF-8")
    public Files getAFile(@PathVariable("classID") String classID, long fid) {
        return filesService.getFile(fid);
    }

    @ApiOperation(value = "下载文件", notes = "")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/download", produces = "application/json;charset=UTF-8")
    public Result down(@PathVariable("classID") String classID, int fid, HttpServletRequest request, HttpServletResponse response) {
        Files files = filesService.getFileWithPath(fid);
        if (!files.getClassID().equals(classID)) {
            return Result.error("本班级中不存在该文件!");
        }
        String path = files.getPath() + files.getName();
        try {
            //获取页面输出流
            ServletOutputStream outputStream = response.getOutputStream();
            //读取文件
            byte[] bytes = FileUtils.readFileToByteArray(new File(path));
            //向输出流写文件
            //写之前设置响应流以附件的形式打开返回值,这样可以保证前边打开文件出错时异常可以返回给前台7
            response.setHeader("Content-Disposition", "attachment;filename=" + files.getName());
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("服务器下载时出错");
        }
        return Result.error("那肯定没开始下载");
    }

    @ApiOperation(value = "查询班级文件列表")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID,'student_'+#classID)")
    @PostMapping(value = "/files", produces = "application/json;charset=UTF-8")
    public List getClassFiles(@PathVariable("classID") String classID) {
        List<Files> files = filesService.getClassFiles(classID);
        return files;
    }

    @ApiOperation(value = "查询班级的签到")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")//和下面保持一致
    @GetMapping(value = "/signIn", produces = "application/json;charset=UTF-8")
    public List<SignIn> getSignIn(@PathVariable("classID") String classID) {

        return signInService.getStuSignIn(classID);
    }

    @ApiOperation(value = "查看某个投票的选项", notes = "适用于老师刚发布投票的场景，老师发布新投票后，直接根据ID请求，就不用去查看全部投票了")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/voteSelections", produces = "application/json;charset=UTF-8")
    public Result VoteSelections(@PathVariable("classID") String classID, long vid) {
        //此处不再是title，注意返回的信息要包含title
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisUtil.zReverseRangeWithScores("class:" + classID + ":" + "type:" + "VoteSelections" + ":" + "vid" + ":" + vid, 0, -1);
        return Result.success("获取投票选项成功", typedTuples);
    }

    @ApiOperation(value = "查看班级投票")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/getVotes", produces = "application/json;charset=UTF-8")
    public List<Vote> receiveAllVotes(@PathVariable("classID") String classID) {
        return voteService.getClassVotes(classID);
    }


}
