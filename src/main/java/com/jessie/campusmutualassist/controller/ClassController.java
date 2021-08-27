package com.jessie.campusmutualassist.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.*;
import com.jessie.campusmutualassist.service.*;
import com.jessie.campusmutualassist.utils.DigestUtil;
import com.jessie.campusmutualassist.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @GetMapping(value = "/getStuList",produces = "application/json;charset=UTF-8")
    public List getStuScores(@PathVariable("classID")String classID)
      {
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
        List<SimpleStu> list=teachingClassService.getSimpleStuList(classID);
        return list;
    }

    @ApiOperation(value = "分类查询当前班级的全部成员",
            notes = "包括老师班长管理员学生")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/ClassMembers",produces = "application/json;charset=UTF-8")
    public JSONObject getClassMembers(@PathVariable("classID")String classID)
    {
        return permissionService.getClassListSortByPermission(classID);
    }
    @ApiOperation(value = "查看班级公告")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/notice",produces = "application/json;charset=UTF-8")
    public Result receiveAllNotices(@PathVariable("classID") String classID){
        List<Notice> noticeList=noticeService.getClassPublicNotices(classID);
        return Result.success("获取公告成功",noticeList);
    }
    @ApiOperation(value = "查看非公开公告（部分人可见）")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/unPublicNotice",produces = "application/json;charset=UTF-8")
    public Result receiveAllUnPublicNotices(@PathVariable("classID") String classID){
        List<Notice> noticeList=noticeService.getClassUnPublicNotices(classID,getCurrentUsername());
        return Result.success("获取非公开公告成功",noticeList);
    }
    @ApiOperation(value = "查看班级公告(以分页形式)")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/noticePage",produces = "application/json;charset=UTF-8")
    public PageInfo receiveAllNoticesByPage(@PathVariable("classID") String classID, @RequestParam(defaultValue = "1")int pageNum){
        PageInfo pageInfo=noticeService.getClassNoticesPage(classID,pageNum);
        return pageInfo;
    }
    @ApiOperation(value = "获取还未参与的公告投票签到")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    //参与英文忘了...
    @GetMapping(value = "/unconfirmed", produces = "application/json;charset=UTF-8")
    public Result noticeUnConfirmed(@PathVariable("classID") String classID) {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("notice",noticeService.getUnConfirmedNotices(classID,getCurrentUsername()));
        jsonObject.put("vote",voteService.getNotVotes(getCurrentUsername(),classID));
        jsonObject.put("signIn",signInService.getMyNotSign(classID,getCurrentUsername()));
        return Result.success("已获取未参与的信息",jsonObject);
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
        String[] info=redisUtil.get("class:" + classID + ":" + "type:" + "RandomSelectInfo").split(":");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("publishedTime",LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(info[0])), ZoneId.systemDefault()));
        jsonObject.put("publisher",info[1]);
        jsonObject.put("students",memSet);
        return Result.success("获取成功",jsonObject);
    }
    @ApiOperation(value = "获取当前班级的最新课堂！随机选人结果")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/CourseRandomStu", produces = "application/json;charset=UTF-8")
    public Result getCourseRandomStu(@PathVariable("classID") String classID) {
        Map<Object, Object> objectObjectMap = redisUtil.hGetAll("class:" + classID + ":" + "type:" + "CourseRandomSelect");
        String[] info=redisUtil.get("class:" + classID + ":" + "type:" + "CourseRandomSelectInfo").split(":");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("publishedTime",LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(info[0])), ZoneId.systemDefault()));
        jsonObject.put("publisher",info[1]);
        jsonObject.put("students",objectObjectMap);
        return Result.success("已获取随机选人结果",jsonObject);//别问，问就是IDE自动生成名字懒得改，虽然这个名字怪怪的
    }
    @ApiOperation(value = "获取还没签到的学生")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID,'student_'+#classID)")
    @PostMapping(value = "/getSignInDetail", produces = "application/json;charset=UTF-8")
    public Result checkSignIn(@PathVariable("classID") String classID, String signID) {
        Set<String> notSingInList = redisUtil.sGetMembers("class:" + classID + ":type:" + "signIn" + ":" + "signId:"+signID);
        return Result.success("未签到的学生", notSingInList);
    }
    @ApiOperation(value = "查询是否自动同意学生加入")
    @PreAuthorize("hasAnyAuthority('teacher','student')")
    @GetMapping(value = "/autoAccept", produces = "application/json;charset=UTF-8")
    public Result autoAcceptStudentJoin(@PathVariable("classID") String classID) {
        return Result.success(redisUtil.hasKey("classID" + ":" + classID + ":type:" + "Auto_AcceptStu").toString());
    }
    @ApiOperation(value = "查询投票每个人对应选项选的啥")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID,'student_'+#classID)")
    @GetMapping(value = "/voterSelections", produces = "application/json;charset=UTF-8")
    public HashMap getVoterResult(@PathVariable("classID") String classID,String vid) {
        Set<String> selections = redisUtil.zRange("class:" + classID + ":" + "type:" + "VoteSelections" + ":" + "vid:" + vid, 0, -1);
        HashMap<String,Set<String>> selectorMap=new HashMap<>();
        for(String selection:selections){
            if(redisUtil.hasKey("class:" + classID + ":" + "type:" + "Voter"+":"+"vid:"+vid+":Selection:"+selection)){
                selectorMap.put(selection,redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "Voter"+":"+"vid:"+vid+":Selection:"+selection));
            }
            else{
                selectorMap.put(selection,null);
            }
        }
        return selectorMap;
    }
    @ApiOperation(value = "单文件直接获取")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID,'student_'+#classID)")
    @GetMapping(value = "/file", produces = "application/json;charset=UTF-8")
    public Files getAFile(@PathVariable("classID") String classID,long fid) {
        return filesService.getFile(fid);
    }

    @ApiOperation(value = "上传文件",notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "upload",value = "上传文件",required = true , dataType = "__file",paramType = "form"),
            @ApiImplicitParam(name="classID",value = "班级的ID",required = true,dataType = "String"),
            @ApiImplicitParam(name="hash",value = "文件哈希值,如果提供，服务器会在上传后比较是否一致，不一致则上传失败",required = false)
    })
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @PostMapping(value = "/upload", produces = "application/json;charset=UTF-8")
    public Result Upload(@PathVariable("classID") String classID,  @RequestParam("upload") MultipartFile upload, @RequestParam(value = "hash",required = false)String hash) throws Exception
    {
        //String path="/usr/camFiles/"+classID+"/";
        String path="D:/camFiles/"+classID+"/";
        String hashCode="";
        File file = new File(path);
        Files files=new Files();
        if (!file.exists())
        {
            file.mkdirs();
        }
        try
        {
            String filename = upload.getOriginalFilename();
            String suffix = filename.substring(filename.lastIndexOf(".") + 1);
            File file2=new File(path+upload.getOriginalFilename());
            upload.transferTo(file2);
            log.info("new file write to disk");
            hashCode=DigestUtil.getSHA256(file2);
            if(hash!=null&&!hash.equals(hashCode)){
                file2.delete();
                return Result.error("文件的hash码不匹配");
            }

            files.setName(upload.getOriginalFilename());
            files.setClassID(classID);
            files.setHash(hashCode);
            files.setPath(path);
            files.setUsername(getCurrentUsername());
            files.setType("其它");
            files.setUploadTime(LocalDateTime.now());
            filesService.newFile(files);
            log.info("new file write to mysql");
        } catch (NullPointerException e)
        {
            e.printStackTrace();
            return Result.error("找不到文件的名字",404);
        } catch (Exception e)
        {
            e.printStackTrace();
            return Result.error("未知错误",500);
        }
        return Result.success("上传成功",files.getFid());
    }
    @ApiOperation(value = "下载文件",notes = "")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/download", produces = "application/json;charset=UTF-8")
    public Result down(@PathVariable("classID")String classID, int fid, HttpServletRequest request,HttpServletResponse response)
    {
        Files files=filesService.getFile(fid);
        String path=files.getPath()+files.getName();
        try
        {
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
        } catch (IOException e)
        {
            e.printStackTrace();
            return Result.error("服务器出错");
        }
        return Result.error("那肯定没开始下载");
    }
    @ApiOperation(value = "查询班级文件列表")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID,'student_'+#classID)")
    @PostMapping(value = "/files", produces = "application/json;charset=UTF-8")
    public List getClassFiles(@PathVariable("classID") String classID){
        List<Files> files=filesService.getClassFiles(classID);
        return files;
    }
    @ApiOperation(value = "快传文件",notes = "快传要传文件的SHA256(用16进制表示，一共64位），同时注意把文件名也要传上来")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @PostMapping(value = "/fastUpload", produces = "application/json;charset=UTF-8")
    public Result FastUpload(HttpServletRequest request,@PathVariable("classID") String classID, String hash,String fileName) throws Exception
    {
        Files files=filesService.getFilesByHash(hash);
        if(files==null){
            return Result.error("快传失败，服务器无此类型文件，使用普通上传");
        }

        try
        {
            files.setName(fileName);
            files.setClassID(classID);
            files.setUsername(getCurrentUsername());
            files.setUploadTime(LocalDateTime.now());
            filesService.newFile(files);
            log.info("new file write to mysql");
        } catch (NullPointerException e)
        {
            e.printStackTrace();
            return Result.error("找不到文件的名字",404);
        } catch (Exception e)
        {
            e.printStackTrace();
            return Result.error("未知错误",500);
        }
        return Result.success("快传成功");
    }
    @ApiOperation(value = "查询班级的签到")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")//和下面保持一致
    @GetMapping(value = "/signIn", produces = "application/json;charset=UTF-8")
    public List<SignIn> getSignIn(@PathVariable("classID") String classID) {
        return signInService.getStuSignIn(classID);
    }
    @ApiOperation(value = "查看某个投票的选项",notes = "适用于老师刚发布投票的场景，老师发布新投票后，直接根据ID请求，就不用去查看全部投票了")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/voteSelections",produces = "application/json;charset=UTF-8")
    public Result VoteSelections(@PathVariable("classID") String classID,long vid){
        //此处不再是title，注意返回的信息要包含title
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisUtil.zReverseRangeWithScores("class:" + classID + ":" + "type:" + "VoteSelections" + ":" + "vid" + ":" + vid, 0, -1);
        return Result.success("获取投票选项成功",typedTuples);
    }
    @ApiOperation(value = "查看班级投票")
    @PreAuthorize("hasAnyAuthority('student_'+#classID,'teacher_'+#classID)")
    @GetMapping(value = "/getVotes",produces = "application/json;charset=UTF-8")
    public List<Vote> receiveAllVotes(@PathVariable("classID") String classID){
        return voteService.getClassVotes(classID);
    }


}
