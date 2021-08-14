package com.jessie.campusmutualassist.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.Notice;
import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.service.NoticeService;
import com.jessie.campusmutualassist.service.StuSelectionService;
import com.jessie.campusmutualassist.service.UserService;
import com.jessie.campusmutualassist.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashSet;
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
    //引出了一个更深的问题，怎么把助教＆学生名单一起打包返回？
    //那么，必定需要，封装一个新的类来进行返回了


    @ApiOperation(value = "查询当前班级的全部学生及其对应真名",
            notes = "建议前端保存名字和username映射关系到本地，节约服务器宝贵资源（）")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/getStuList",produces = "application/json;charset=UTF-8")
    public Result getStuList(@PathVariable("classID")String classID)
    {
        Set<String> mySet;
        if(redisUtil.hasKey("class:" + classID + ":" + "type:" + "members")){
        mySet=redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "members");
        }
        else{
            mySet=new HashSet<>(stuSelectionService.getClassSelectStuName(classID));
            redisUtil.sAdd("class:" + classID + ":" + "type:" + "members",mySet.toArray(new String[0]));
        }
        List<Map<String, String>> realNameWithUsername = userService.getRealNameWithUsername(mySet);
        //查询时，应该带上老师
        return Result.success("查询已加入学生成功(附带名字)",realNameWithUsername);
    }
    @ApiOperation(value = "查看班级公告")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/notice",produces = "application/json;charset=UTF-8")
    public Result receiveAllNotices(@PathVariable("classID") String classID){
        List<Notice> noticeList=noticeService.getClassNotices(classID);
        return Result.success("获取公告成功",noticeList);
    }
    @ApiOperation(value = "查看班级公告(以分页形式)")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/noticePage",produces = "application/json;charset=UTF-8")
    public PageInfo receiveAllNoticesByPage(@PathVariable("classID") String classID, @RequestParam(defaultValue = "1")int pageNum){
        PageInfo pageInfo=noticeService.getClassNoticesPage(classID,pageNum);
        return pageInfo;
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
    @ApiOperation(value = "查询是否自动同意学生加入")
    @PreAuthorize("hasAnyAuthority('teacher','student')")
    @GetMapping(value = "/autoAccept", produces = "application/json;charset=UTF-8")
    public Result autoAcceptStudentJoin(@PathVariable("classID") String classID) {
        return Result.success(redisUtil.hasKey("classID" + ":" + classID + ":type:" + "Auto_AcceptStu").toString());
    }

    @ApiOperation(value = "上传文件",notes = "尚未完成！")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @PostMapping(value = "/upload", produces = "text/html;charset=UTF-8")
    public String UploadImg(HttpServletRequest request,@PathVariable("classID") String classID, @RequestParam("upload") MultipartFile upload) throws Exception
    {
//        System.out.println("上传头像");
        String username=getCurrentUsername();
        String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        System.out.println(path);
        //如果文件重名，应该覆盖原文件吧（是否覆盖由前端决定）
        //选的是war exploded 那么文件会在工程目录下
        //否则在tomcat目录下

        File file = new File(path);
        if (!file.exists())
        {
            file.mkdirs();
        }
        try
        {
            String filename = upload.getOriginalFilename();
            String suffix = filename.substring(filename.lastIndexOf(".") + 1);
//            String userFileName = uid + getCurrentUsername() + "." + suffix;
//            if (!theImgSuffix.containsValue(suffix))
//            {
//                throw new Exception("?");
//            }
//            User user = new User();
//            user.setUid(uid);
//            user.setImg_path(path + "/" + userFileName);
//            upload.transferTo(new File(path, userFileName));
//            userService.saveImg(user);
//            System.out.println("头像保存成功，开始向数据库中更新用户数据");


        } catch (NullPointerException e)
        {
            e.printStackTrace();
            return JSON.toJSONString(Result.error("找不到文件的名字"));
        } catch (Exception e)
        {
            e.printStackTrace();
            return JSON.toJSONString(Result.error("未知错误"));
        }
        return JSON.toJSONString(Result.success("上传成功"));
    }

}
