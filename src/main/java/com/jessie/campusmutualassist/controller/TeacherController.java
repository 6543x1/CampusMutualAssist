package com.jessie.campusmutualassist.controller;

import com.alibaba.fastjson.JSON;
import com.jessie.campusmutualassist.aop.PointsOperationLog;
import com.jessie.campusmutualassist.entity.*;
import com.jessie.campusmutualassist.entity.myEnum.SignType;
import com.jessie.campusmutualassist.service.*;
import com.jessie.campusmutualassist.service.impl.AliyunGreenService;
import com.jessie.campusmutualassist.service.impl.DumpService;
import com.jessie.campusmutualassist.service.impl.PushService;
import com.jessie.campusmutualassist.utils.JwtTokenUtil;
import com.jessie.campusmutualassist.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jessie.campusmutualassist.service.impl.MailServiceImpl.getRandomString;
import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;

@Api(tags = "老师通用操作")
@RestController
@RequestMapping("/classes")
public class TeacherController {

    @Autowired
    TeachingClassService teachingClassService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    StuSelectionService stuSelectionService;
    @Autowired
    NoticeService noticeService;
    @Autowired
    StudentPointsService studentPointsService;
    @Autowired
    MailService mailService;
    @Autowired
    VoteService voteService;
    @Autowired
    SignInService signInService;
    @Autowired
    DumpService dumpService;
    @Autowired
    StuPointsDetailService stuPointsDetailService;
    @Autowired
    PushService pushService;
    @Autowired
    UserService userService;
    @Autowired
    AliyunGreenService aliyunGreenService;

    static final Random random = new Random();

    /*
      paramType是参数的请求类型，这是枚举类型，包括以下内容：
      HEADER("header"),
      PATH("path"),
      COOKIE("cookie"),
      FORM("form"),
      FORMDATA("formData"),
      BODY("body");）
      所以，不要把dataType和paramType弄混了！
        */
    @ApiOperation(value = "创建班级")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "班级名字", dataType = "String"),
            @ApiImplicitParam(name = "schedule", value = "班级上课时间地点", dataType = "String")
    })
    @PreAuthorize("hasAnyAuthority('teacher')")
    @PostMapping(value = "/createClass", produces = "application/json;charset=UTF-8")
    public Result createClass(TeachingClass teachingClass, HttpServletRequest request) {
        try {
            String username = getCurrentUsername();
            teachingClass.setTeacher(username);
            teachingClass.setId(getRandomString());
            teachingClassService.createClass(teachingClass);
            permissionService.setUserPermission(username, "teacher_" + teachingClass.getId());

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建失败，请检查是否有误后重试");
        }
        //redisUtil.sAdd("class:"+teachingClass.getId()+"type:"+"members",getCurrentUsername());//一定要把老师自己加入！
        return Result.success("班级创建成功", teachingClass.getId());
    }

    @ApiOperation(value = "获取当前账号创建的班级")
    @PreAuthorize("hasAnyAuthority('teacher')")
    @GetMapping(value = "/getMyCreatedClass", produces = "application/json;charset=UTF-8")
    public Result getCreatedClassInfo() {
        List<TeachingClass> myClass = teachingClassService.getCreatedClass(getCurrentUsername());
        return Result.success("查询老师创建的课程成功", myClass);
    }

    @ApiOperation(value = "以Excel方式导入学生名单")
    @PreAuthorize("hasAnyAuthority('teacher'+#classID)")
    @PostMapping(value = "/{classID}/addStudents", produces = "application/json;charset=UTF-8")
    public Result addStudents(@PathVariable("classID") String classID, @RequestParam("excel") MultipartFile excel) throws Exception {
        System.out.println(TeacherController.class.getClassLoader().getResource(""));
        File file = new File(TeacherController.class.getResource("").toString().substring(5) + classID + "/");
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(file, excel.getOriginalFilename());
        excel.transferTo(file2);
        String suffix = excel.getOriginalFilename();
        suffix = suffix.substring(suffix.lastIndexOf(".") + 1);
        Workbook workbook;
        if ("xlsx".equals(suffix)) {
            workbook = new XSSFWorkbook(file2);
        } else if ("xls".equals(suffix)) {
            FileInputStream fis = new FileInputStream(file2);
            workbook = new HSSFWorkbook(fis);
        } else {
            return Result.error("ERROR");
        }
        Sheet sheet = workbook.getSheetAt(0);     //读取sheet 0

        int firstRowIndex = sheet.getFirstRowNum() + 1;   //第一行是列名，所以不读
        int lastRowIndex = sheet.getLastRowNum();
        System.out.println("firstRowIndex: " + firstRowIndex);
        System.out.println("lastRowIndex: " + lastRowIndex);

        Row row0 = sheet.getRow(firstRowIndex);
        int theNoIndex = 0;
        if (row0 != null) {
            int firstCellIndex = row0.getFirstCellNum();
            int lastCellIndex = row0.getLastCellNum();
            for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
                Cell cell = row0.getCell(cIndex);
                if (cell != null && cell.toString().contains("学号")) {
                    theNoIndex = cIndex;
                    System.out.println("学号位于" + theNoIndex);
                    break;
                }
            }
        }
        ArrayList<String> stuNameList = new ArrayList<>();
        ArrayList<String> notJoinList = new ArrayList<>();
        for (int rIndex = firstRowIndex + 1; rIndex <= lastRowIndex; rIndex++) {   //遍历行
            System.out.println("rIndex: " + rIndex);
            Row row = sheet.getRow(rIndex);
            if (row != null) {
                Cell cell = row.getCell(theNoIndex);

                if (cell != null) {
                    cell.setCellType(CellType.STRING);//强转String了。。这玩意自动把长数字文本识别成数字

                    System.out.println(cell.toString());
                    try {
                        stuSelectionService.newSelections(new StuSelection(cell.toString(), "", classID, 0));
                        stuNameList.add(cell.toString() + "@fzu.edu.cn");
                        //System.out.println(cell.getCellTypeEnum());
                        if (cell.toString().contains(".")) {
                            System.out.println("学号所在的列不是文本型，导入失败！请调整为文本型后重新上传");
                        }
                    } catch (Exception e) {
                        notJoinList.add(cell.toString());
                    }
                    //非文本型读取后会变成数字(强行toString情况下），有时候变成科学计数法了。。。。并且缺失掉0，实在是懒得做这个检测
                    //所以只能要求硬性设置为文本型
                }
            }
        }
        redisUtil.sAdd("class:" + classID + "type:" + "members", (String[]) stuNameList.toArray());
        file2.delete();
        if (notJoinList.size() != 0) {
            return Result.error("部分学生未导入成功", 400, notJoinList);
        } else {
            return Result.success("学生导入成功");
        }
    }

    @ApiOperation(value = "导出班级活跃分")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/dumpStuPoints")
    public Result dumpStuPoints(@PathVariable("classID") String classID, HttpServletResponse response) throws Exception {
        dumpService.dumpClassPointsXlsx(classID, getCurrentUsername(), classID);
        return Result.success("后台导出中，稍后可以在班级文件中看到。");
    }

    @ApiOperation(value = "获取当前待加入的学生列表")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @GetMapping(value = "/{classID}/getJoinList", produces = "application/json;charset=UTF-8")
    public Result getCreatedClassInfo(@PathVariable("classID") String classID) {
        Set<String> mySet = redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "JoinQueue");
        return Result.success("查询待加入学生成功", mySet);
    }

    @ApiOperation(value = "同意学生加入班级", notes = "students可以有多个值")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/acceptStudents", produces = "application/json;charset=UTF-8")
    public Result acceptStudents(@PathVariable("classID") String classID, @RequestParam("students") Set<String> students) {
        //此处应该启动一个新的线程吧?
        for (String student : students) {
            try {
                stuSelectionService.newSelections(new StuSelection(student, "", classID, 0));
                permissionService.setUserPermission(student, "student_" + classID);
                redisUtil.sAdd("class:" + classID + ":" + "type:" + "members", student);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        redisUtil.sRemove("class:" + classID + ":" + "type:" + "JoinQueue", students.toArray());
        //待加入的暂时存放于数据库中，然后传入时比对是否有待加入存在，否则无法加入
        return Result.success("同意成功");
    }

    @ApiOperation(value = "驱逐学生(踢出班级)")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/removeStu", produces = "application/json;charset=UTF-8")
    public Result removeStu(@PathVariable("classID") String classID, String username) {
        stuSelectionService.quitClass(classID, username);
        pushService.pushWechatMessage(Collections.singleton(username), "你被踢出班级:" + classID);
        return Result.success("驱逐成功");
    }

    @ApiOperation(value = "自动同意学生加入")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/autoAccept", produces = "application/json;charset=UTF-8")
    public Result autoAcceptStudentJoin(@PathVariable("classID") String classID, boolean enable) {
        if (enable == true) {
            redisUtil.set("classID" + ":" + classID + ":type:" + "Auto_AcceptStu", "true", 24 * 60 * 60);
        } else {
            try {
                redisUtil.delete("classID" + ":" + classID + ":type:" + "Auto_AcceptStu");
            } catch (Exception e) {
                e.printStackTrace();

            }
            return Result.error("自动加入已经失效");
        }
        //待加入的暂时存放于数据库中，然后传入时比对是否有待加入存在，否则无法加入
        return Result.success("已经允许自动加入，24小时有效");
    }

    @ApiOperation(value = "创建投票")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/createVote", produces = "application/json;charset=UTF-8")
    public Result createVote(@PathVariable("classID") String classID, String title,
                             @RequestParam("selections") Set<String> selections, int limitation,
                             boolean anonymous, @RequestParam(defaultValue = "604800") int minTimes) {
        //redisUtil.sAdd("class:" + classID + ":" + "type:" + "Vote",title);
        if (minTimes <= 30) {
            return Result.error("时间太短，不要小于30秒", 400);
        }
        LocalDateTime deadLine = LocalDateTime.now().plusSeconds(minTimes + random.nextInt(10));
        Vote vote = Vote.builder()
                .classID(classID)
                .title(title)
                .limitation(limitation)
                .Selections(selections)
                .publishedTime(LocalDateTime.now())
                .anonymous(anonymous)
                .publisher(getCurrentUsername())
                .deadLine(deadLine)
                .build();
        //舒服了，不然长的和杀人书一样
        voteService.newVote(vote);
        System.out.println(vote.getVid());
        for (String x : selections) {
            redisUtil.zAdd("class:" + classID + ":" + "type:" + "VoteSelections" + ":" + "vid:" + vote.getVid(),
                    x, 0);
        }
        redisUtil.set("scheduledTask:" + "VoteDeadLine" + ":" + "Vid:" + vote.getVid(),
                String.valueOf(vote.getVid()), minTimes + random.nextInt(10));
        //redisUtil.sAdd("class:" + classID + ":" + "type:" + "Voter"+":"+"title:"+title,"No more persons...");
        //请勿添加过多选项，以免响应缓慢
        pushService.pushSocketMessage(redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "members"),
                JSON.toJSONString(Result.success("新的投票", vote.getVid())));
        return Result.success("投票已经开始", vote.getVid());
    }

    @ApiOperation(value = "删除投票", notes = "有管理员权限就行，不考虑管理员内讧")
    @PreAuthorize("hasAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/deleteVote", produces = "application/json;charset=UTF-8")
    public Result deleteVote(@PathVariable("classID") String classID, long vid) {
        voteService.deleteVote(classID, vid);
        return Result.success("删除成功!");
    }

    @ApiOperation(value = "发布公告")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")//和下面保持一致
    @PostMapping(value = "/{classID}/publishNotice", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", required = true),
            @ApiImplicitParam(name = "body", required = true),
            @ApiImplicitParam(name = "confirm", required = true, dataType = "boolean"),
            @ApiImplicitParam(name = "minTimes", value = "单位：秒，超过这个时间后自动提醒未完成的学生"),
            @ApiImplicitParam(name = "type", value = "分类"),
            @ApiImplicitParam(name = "public", value = "是否公开（选否需要提供可以查看的人）", required = true),
            @ApiImplicitParam(name = "students", value = "如果isPublic=false需要提供可以查看的学生", required = false)

    })
    public Result publishNotice(@PathVariable("classID") String classID, Notice notice,
                                @RequestParam(required = false, defaultValue = "0") int minTimes,
                                @RequestParam(required = false) List<String> students) {
        if (minTimes != 0 && minTimes < 179) {
            return Result.error("提醒时间不宜小于三分钟，请设置的长一些。");
        }
        notice.setClassID(classID);
        notice.setPublishedTime(LocalDateTime.now());
        notice.setPublisher(getCurrentUsername());
        if (minTimes != 0) {
            notice.setDeadLine(LocalDateTime.now().plusSeconds(minTimes));
        }
        Result textSafeResult = aliyunGreenService.testTextSafe(notice.getBody());
        if (!textSafeResult.isStatus()) {
            if (textSafeResult.getCode() != 400) {
                return textSafeResult;
            } else {
                notice.setBody(textSafeResult.getData().toString());
            }
        }
        if (notice.isPublic()) {
            noticeService.newNotice(notice);
        } else {
            noticeService.newUnPublicNotice(notice, students);
        }
        if (minTimes != 0) {
            redisUtil.set("scheduledTask:" + "noticeUrge:" + "classID:" + classID + ":" + "nid" + ":" + notice.getNid(), classID, minTimes + random.nextInt(10));
        }
        //待加入的暂时存放于数据库中，然后传入时比对是否有待加入存在，否则无法加入
        //Redis
        pushService.pushSocketMessage(redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "members"),
                JSON.toJSONString(Result.success("新的公告", notice.getNid())));
        return Result.success("公告已发布");
    }

    @ApiOperation(value = "删除公告", notes = "有管理员权限就行，不考虑管理员内讧")
    @PreAuthorize("hasAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/deleteNotice", produces = "application/json;charset=UTF-8")
    public Result deleteNotice(@PathVariable("classID") String classID, long nid) {
        noticeService.deleteNotice(nid, classID);
        return Result.success("删除成功!");
    }

    @ApiOperation(value = "催还没读公告的")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")//和下面保持一致
    @PostMapping(value = "/{classID}/notice/{nid}/urge", produces = "application/json;charset=UTF-8")
    public Result urgeNotice(@PathVariable("nid") int nid, @PathVariable("classID") String classID) {
        String classID2 = noticeService.getClassID(nid);
        Set<String> notConfirmed = redisUtil.sDifference("class:" + classID2 + ":type:" + "noticeConfirmed" + ":" + "nid:" + nid, "class:" + classID + ":" + "type:" + "members");
        noticeService.urge(notConfirmed);
        pushService.pushUrgeWechatMessage(notConfirmed, "公告", "老师催你了去读公告了！", "无摘要");
        return Result.success("在催了在催了");
    }

    @ApiOperation(value = "催还没投票的的")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")//{title}待持久化到数据库后，会以ID取代之
    @PostMapping(value = "/{classID}/vote/{title}/urge", produces = "application/json;charset=UTF-8")
    public Result urgeVote(@PathVariable("vid") long vid, @PathVariable("classID") String classID) {
        Set<String> notConfirmed = redisUtil.sDifference("class:" + classID + ":" + "type:" + "Voter" + ":" + "vid:" + vid, "class:" + classID + ":" + "type:" + "members");
        mailService.urgeStu(notConfirmed, "快去投票！");
        pushService.pushUrgeWechatMessage(notConfirmed, "投票", "老师催你了去投票了！", "无摘要");
        return Result.success("在催了在催了");
    }

    @ApiOperation("活动随机选人")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "num", value = "选取的人数")
    }
    )
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/RandomStu", produces = "application/json;charset=UTF-8")
    public Result RandomStu(@PathVariable("classID") String classID, @RequestParam(value = "num", defaultValue = "1") int num) {
        List<String> strings = redisUtil.sRandomMembers("class:" + classID + ":" + "type:" + "members", num);
        //随机选人结果应该所有人都可见，否则具有不公平性
        redisUtil.sAdd("class:" + classID + ":" + "type:" + "RandomSelect", strings.toArray(new String[0]));
        redisUtil.expire("class:" + classID + ":" + "type:" + "RandomSelect", 24 * 60 * 60, TimeUnit.SECONDS);
        redisUtil.set("class:" + classID + ":" + "type:" + "RandomSelectInfo", System.currentTimeMillis() + ":" + getCurrentUsername());
        //建议有多次选人，直接截图吧.....
        //其实本来这东西应该直接Push到客户端的，服务器不该保存到Redis中的......搞的现在前端得不断轮询服务器。。。。。
        pushService.pushSocketMessage(new HashSet<>(strings), "你被选中了");
        pushService.pushSocketMessage(redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "members"), "被选中的人：" + strings);
        return Result.success("已抽取", strings);
    }

    @ApiOperation(value = "课堂随机选人", notes = "一次只能选一个，以115分钟为单位,注意结果为了避免重复，会在后面附加时间戳，请去掉:后面的内容")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/CourseRandomStu", produces = "application/json;charset=UTF-8")
    public Result RandomStuInCourse(@PathVariable("classID") String classID) {
        String unLuckyGuy = redisUtil.sRandomMember("class:" + classID + ":" + "type:" + "members");
        //随机选人结果应该所有人都可见，否则具有不公平性
        String string = unLuckyGuy + ":" + System.currentTimeMillis();//避免重复选到同一个人导致被覆盖
        redisUtil.hPut("class:" + classID + ":" + "type:" + "CourseRandomSelect", string, "NULL");
        redisUtil.expire("class:" + classID + ":" + "type:" + "CourseRandomSelect", 125 * 60, TimeUnit.SECONDS);
        redisUtil.set("class:" + classID + ":" + "type:" + "CourseRandomSelectInfo", System.currentTimeMillis() + ":" + getCurrentUsername());
        pushService.pushSocketMessage(Collections.singleton(string), "你被老师选中了");
        pushService.pushSocketMessage(redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "members"), "被选中的人：" + string);

        return Result.success("已抽取", string);
    }

    @ApiOperation(value = "课堂随机选人加活跃分", notes = "会直接体现在随机选人的全部结果中，传入时，连带username:时间戳")
    @PointsOperationLog(module = "课堂回答加分", type = "common", desc = "课堂加分")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/addCourseRandomStu", produces = "application/json;charset=UTF-8")
    public Result addRandomStuPointsInCourse(@PathVariable("classID") String classID, String student, int points) {
        if (points > 3 || points < -3) {
            return Result.error("分数不对劲");
        }
        student = student.split(":")[0];
        redisUtil.hPut("class:" + classID + ":" + "type:" + "CourseRandomSelect", student, String.valueOf(points));
        studentPointsService.addStusPoints(Collections.singleton(student), classID, points);
        //此处要@Async，插入操作肯定要异步了
//        StuPointsDetail stuPointsDetail=new StuPointsDetail();
//        stuPointsDetail.setPoints(points);
//        stuPointsDetail.setClassID(classID);
//        stuPointsDetail.setOperator(getCurrentUsername());
//        stuPointsDetail.setReason("课堂回答加分");
//        stuPointsDetailService.newDetail(stuPointsDetail, Collections.singleton(student));
        return Result.success("加分完成");
    }


    @ApiOperation(value = "加活跃分", notes = "活跃分上限为100，超过自动记为100分")
    @PointsOperationLog(module = "老师加分", type = "common", desc = "普通加分")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/addStuPoints", produces = "application/json;charset=UTF-8")
    public Result addStuPoints(@PathVariable("classID") String classID, @RequestParam("students") Set<String> students, int points, @RequestParam(defaultValue = "加分") String reason) {
        if (points < -100 || points > 100) {
            return Result.error("分数超出范围！");
        }
        if (students.size() == 0) {
            return Result.error("学生为空");
        }
        studentPointsService.addStusPoints(students, classID, points);
        //此处要@Async，插入操作肯定要异步了
//        StuPointsDetail stuPointsDetail=new StuPointsDetail();
//        stuPointsDetail.setPoints(points);
//        stuPointsDetail.setClassID(classID);
//        stuPointsDetail.setOperator(getCurrentUsername());
//        stuPointsDetail.setReason(reason);
//
//        stuPointsDetailService.newDetail(stuPointsDetail,students);
        return Result.success("已加分");
    }

    @ApiOperation(value = "清空活跃分到60", notes = "会清空当前时间之前的全部记录！需要再次输入密码")
    @PointsOperationLog(module = "清空全部分数", type = "common", desc = "清空")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/remakeStuPoints", produces = "application/json;charset=UTF-8")
    public Result clearStuPoints(@PathVariable("classID") String classID, String password) {
        if (userService.cmpPassword(getCurrentUsername(), password)) {
            studentPointsService.remakePoints(classID);//同时会清空当前时间前的全部记录,
            stuPointsDetailService.deleteOldItems(classID);
            return Result.success("已加分");
        }
        return Result.error("密码错误，需要正确的密码才能清空！", 403);
    }

    @ApiOperation(value = "发布签到")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/publishSignIn", produces = "application/json;charset=UTF-8")
    public Result publishSignIn(@PathVariable("classID") String classID, String title, String key, @RequestParam(defaultValue = "180") long expiredTime) {
        //签到估计要重写;签到信息得序列化到数据库中;
        //签到还要记录发布时间啥的信息。这个估计也要记录一下；其实不记录也可以
        if (expiredTime < 30) {
            return Result.error("签到时间过短！");
        }
        SignIn signIn = new SignIn();
        signIn.setClassID(classID);
        signIn.setSignKey(key);
        signIn.setTitle(title);
        signIn.setDeadLine(LocalDateTime.now().plusSeconds(expiredTime + random.nextInt(5)));
        signIn.setPublisher(getCurrentUsername());
        //时间交给mysql自己生成了
        if ("0".equals(key)) {
            signIn.setSignType(SignType.normal);
        } else {
            signIn.setSignType(SignType.key);
        }
        signInService.newSignIn(signIn);
        Set<String> memSet = redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "members");
        if (memSet.size() == 0) {
            return Result.error("没有获取到班级成员信息");
        }
        String[] members = memSet.toArray(new String[0]);
        redisUtil.sAdd("class:" + classID + ":type:" + "signIn" + ":" + "signId:" + signIn.getSignID(), members);
        //redisUtil.expire("class:" + classID + ":type:" + "signIn" + ":" +  "signId:"+signIn.getSignID(), expiredTime, TimeUnit.SECONDS);
        redisUtil.set("scheduledTask:" + "signInExpire:" + "classID:" + classID + ":" + "signID" + ":" + signIn.getSignID(), classID, expiredTime + random.nextInt(3));

        pushService.pushSocketMessage(memSet,
                JSON.toJSONString(Result.success("新的签到", signIn.getSignID())));
        return Result.success("签到已经发布了", signIn.getSignID());
    }

    @ApiOperation(value = "删除签到", notes = "有管理员权限就行，不考虑管理员内讧")
    @PreAuthorize("hasAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/deleteSign", produces = "application/json;charset=UTF-8")
    public Result deleteSign(@PathVariable("classID") String classID, long signID) {
        signInService.deleteSignIn(classID, signID);
        return Result.success("删除成功!");
    }

    @ApiOperation(value = "补签", notes = "一次签一个")
    @PreAuthorize("hasAuthority('teacher_'+#classID) AND hasAuthority('teacher')")
    @PostMapping(value = "/{classID}/supplySign", produces = "application/json;charset=UTF-8")
    public Result supplySign(@PathVariable("classID") String classID, long signID, String student) {
        if (redisUtil.hasKey("class:" + classID + ":type:" + "signIn" + ":" + "signID:" + signID)) {
            redisUtil.sRemove("class:" + classID + ":type:" + "signIn" + ":" + "signID:" + signID, student);
        }
        return Result.success("补签成功!");
    }

    @ApiOperation(value = "删除班级（需要验证密码）")
    @PreAuthorize("hasAuthority('teacher_'+#classID) AND hasAuthority('teacher')")
    @PostMapping(value = "/{classID}/deleteClass", produces = "application/json;charset=UTF-8")
    public Result deleteClass(@PathVariable("classID") String classID, String password) {
        if (userService.cmpPassword(getCurrentUsername(), password)) {
            teachingClassService.deleteClass(classID);
            return Result.success("班级已经删除");
        }
        return Result.error("密码不对");
    }

    @ApiOperation(value = "设置管理员", notes = "最多10个")
    @PreAuthorize("hasAuthority ('teacher_'+#classID) AND hasAuthority('teacher') OR hasAuthority ('monitor_'+#classID) AND hasAuthority('monitor')")
    //@PreAuthorize("(hasAuthority('teacher_'+#classID) and hasAuthority('teahcer')) OR (hasAuthority ('monitor_'+#classID) AND hasAuthority('monitor'))")
    @PostMapping(value = "/{classID}/setAssistant", produces = "application/json;charset=UTF-8")
    public Result setAssistant(@PathVariable("classID") String classID, @RequestParam("assistants") List<String> assistants) {
        //这个就不异步了吧
        assistants.forEach((assistant) -> {
            permissionService.setUserPermission(assistant, "teacher_" + classID);
        });
        return Result.success("设置成功");
    }

    @ApiOperation(value = "撤销管理员", notes = "可以一次全删除")
    @PreAuthorize("hasAuthority ('teacher_'+#classID) AND hasAuthority('teacher') OR hasAuthority ('monitor_'+#classID) AND hasAuthority('monitor')")
    @PostMapping(value = "/{classID}/cancelAssistant", produces = "application/json;charset=UTF-8")
    public Result cancelAssistant(@PathVariable("classID") String classID, @RequestParam("assistants") Set<String> assistants) {
        permissionService.deleteUsersPermission(assistants, "teacher_" + classID);
        return Result.success("撤销管理员成功(请稍候刷新");
    }


}
