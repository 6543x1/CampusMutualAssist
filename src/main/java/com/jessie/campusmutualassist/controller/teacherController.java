package com.jessie.campusmutualassist.controller;

import com.jessie.campusmutualassist.entity.*;
import com.jessie.campusmutualassist.service.*;
import com.jessie.campusmutualassist.service.StudentPointsService;
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
import java.io.File;
import java.io.FileInputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jessie.campusmutualassist.service.impl.MailServiceImpl.getRandomString;
import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;
@Api(tags = "老师通用操作")
@RestController
@RequestMapping("/classes")
public class teacherController {

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

    static final Random random = new Random();
    @ApiOperation(value = "创建班级")
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
        return Result.success("班级创建成功",teachingClass.getId());
    }
    @ApiOperation(value = "获取当前账号创建的班级")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @GetMapping(value = "/getMyCreatedClass",produces = "application/json;charset=UTF-8")
    public Result getCreatedClassInfo()
    {
        List<TeachingClass> myClass=teachingClassService.getCreatedClass(getCurrentUsername());
        return Result.success("查询老师创建的课程成功",myClass);
    }
    @ApiOperation(value = "以Excel方式导入学生名单")
    @PreAuthorize("hasAnyAuthority('teacher'+#classID)")
    @PostMapping(value = "/{classID}/addStudents", produces = "application/json;charset=UTF-8")
    public Result addStudents(@PathVariable ("classID") String classID, @RequestParam("excel")MultipartFile excel) throws Exception {
        System.out.println(teacherController.class.getClassLoader().getResource(""));
        File file=new File(teacherController.class.getResource("").toString().substring(5)+classID+"/");
        if(!file.exists()){
            file.mkdirs();
        }
        File file2=new File(file,excel.getOriginalFilename());
        excel.transferTo(file2);
        String suffix=excel.getOriginalFilename();
        suffix=suffix.substring(suffix.lastIndexOf(".")+1);
        Workbook workbook;
        if("xlsx".equals(suffix)){
            workbook=new XSSFWorkbook(file2);
        }
        else if("xls".equals(suffix)){
            FileInputStream fis = new FileInputStream(file2);
            workbook=new HSSFWorkbook(fis);
        }
        else {
            return Result.error("ERROR");
        }
        Sheet sheet = workbook.getSheetAt(0);     //读取sheet 0

        int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
        int lastRowIndex = sheet.getLastRowNum();
        System.out.println("firstRowIndex: "+firstRowIndex);
        System.out.println("lastRowIndex: "+lastRowIndex);

        Row row0=sheet.getRow(firstRowIndex);
        int theNoIndex=0;
        if(row0!=null){
            int firstCellIndex = row0.getFirstCellNum();
            int lastCellIndex = row0.getLastCellNum();
            for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
                Cell cell = row0.getCell(cIndex);
                if (cell != null&&cell.toString().contains("学号")) {
                    theNoIndex=cIndex;
                    System.out.println("学号位于"+theNoIndex);
                    break;
                }
            }
        }
        ArrayList<String> stuNameList=new ArrayList<>();
        ArrayList<String> notJoinList=new ArrayList<>();
        for (int rIndex = firstRowIndex+1; rIndex <= lastRowIndex; rIndex++) {   //遍历行
            System.out.println("rIndex: " + rIndex);
            Row row = sheet.getRow(rIndex);
            if (row != null) {
                Cell cell = row.getCell(theNoIndex);

                if (cell != null) {
                    cell.setCellType(CellType.STRING);//强转String了。。这玩意自动把长数字文本识别成数字

                    System.out.println(cell.toString());
                    try {
                        stuSelectionService.newSelections(new StuSelection(cell.toString(), classID, 0));
                        stuNameList.add(cell.toString() + "@fzu.edu.cn");
                        //System.out.println(cell.getCellTypeEnum());
                        if (cell.toString().contains(".")) {
                            System.out.println("学号所在的列不是文本型，导入失败！请调整为文本型后重新上传");
                        }
                    }catch (Exception e){
                        notJoinList.add(cell.toString());
                    }
                    //非文本型读取后会变成数字(强行toString情况下），有时候变成科学计数法了。。。。并且缺失掉0，实在是懒得做这个检测
                    //所以只能要求硬性设置为文本型
                }
            }
        }
        redisUtil.sAdd("class:"+classID+"type:"+"members", (String[]) stuNameList.toArray());
        file2.delete();
        if(notJoinList.size()!=0){
            return Result.error("部分学生未导入成功",400,notJoinList);
        }
        else{
        return Result.success("学生导入成功");}
    }
    @PostMapping(value = "/{classID}/dumpStuPoints")
    public Result dumpStuPoints(@PathVariable("classID") String classID) throws Exception {
        String path = teacherController.class.getResource("").toString().substring(5) + classID + "/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(path, classID + ".xlsx");
        Workbook workbook = new XSSFWorkbook(file2);
        Sheet sheet = workbook.getSheetAt(0);     //读取sheet 0

        int firstRowIndex = sheet.getFirstRowNum() + 1;   //第一行是列名，所以不读
        int lastRowIndex = sheet.getLastRowNum();
        System.out.println("firstRowIndex: " + firstRowIndex);
        System.out.println("lastRowIndex: " + lastRowIndex);
        Row firstRow = sheet.getRow(0);
        Cell cell1 = firstRow.getCell(0);
        cell1.setCellValue("学号");
        Cell cell2 = firstRow.getCell(1);
        cell2.setCellValue("姓名");
        Cell cell3 = firstRow.getCell(2);
        cell3.setCellValue("Points");
        Row row0 = sheet.getRow(firstRowIndex);
        int theNoIndex = 0;
        List<StuPointsWithRealName> stuPointslist = studentPointsService.StusPoints(classID);
        for (int rIndex = 1; rIndex <= stuPointslist.size(); rIndex++) {   //遍历行
            System.out.println("rIndex: " + rIndex);
            Row row = sheet.getRow(rIndex);
            Cell cellNo = row.getCell(0);
            cellNo.setCellType(CellType.STRING);
            cell1.setCellValue(stuPointslist.get(rIndex).getUsername());
            Cell cellName = row.getCell(1);
            cell2.setCellValue(stuPointslist.get(rIndex).getRealName());
            Cell cellPoints = row.getCell(2);
            cell3.setCellValue(stuPointslist.get(rIndex).getPoints());
            //非文本型读取后会变成数字(强行toString情况下），有时候变成科学计数法了。。。。并且缺失掉0，实在是懒得做这个检测
        }       //所以只能要求硬性设置为文本型



        return null;
    }
    @ApiOperation(value = "获取当前待加入的学生列表")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @GetMapping(value = "/{classID}/getJoinList",produces = "application/json;charset=UTF-8")
    public Result getCreatedClassInfo(@PathVariable("classID")String classID)
    {
        Set<String> mySet=redisUtil.sGetMembers("class:" + classID + ":" + "type:" + "JoinQueue");
        return Result.success("查询待加入学生成功",mySet);
    }
    @ApiOperation(value = "同意学生加入班级",notes = "students可以有多个值")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/acceptStudents", produces = "application/json;charset=UTF-8")
    public Result acceptStudents(@PathVariable("classID") String classID, @RequestParam("students") Set<String> students) {
        //此处应该启动一个新的线程吧?
        for (String student : students) {
            try {
                stuSelectionService.newSelections(new StuSelection(student, classID, 0));
                permissionService.setUserPermission(student,"student_"+classID);
                redisUtil.sAdd("class:"+classID+":"+"type:"+"members",student);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        redisUtil.sRemove("class:" + classID + ":" + "type:" + "JoinQueue", students.toArray());
        //待加入的暂时存放于数据库中，然后传入时比对是否有待加入存在，否则无法加入
        return Result.success("同意成功");
    }
    @ApiOperation(value = "自动同意学生加入，目前不知道行不行")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/enableAutoAcceptStudentJoin", produces = "application/json;charset=UTF-8")
    public Result autoAcceptStudentJoin(@PathVariable("classID") String classID,boolean enable) {
        if(enable==true) {
            redisUtil.set("classID"+":"+classID+":type:"+"Auto_AcceptStu","true",24*60*60);
        } else {
            redisUtil.delete("classID"+":"+classID+":type:"+"Auto_AcceptStu");
        }
        //待加入的暂时存放于数据库中，然后传入时比对是否有待加入存在，否则无法加入
        return Result.success("已经允许自动加入，24小时有效");
    }
    @ApiOperation(value = "创建投票")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/createVote", produces = "application/json;charset=UTF-8")
    public Result createVote(@PathVariable("classID") String classID, String title, @RequestParam("selections") Set<String> selections,int limit,boolean anonymous) {
        redisUtil.sAdd("class:" + classID + ":" + "type:" + "Vote",title);
        HashMap<String,String> voteArgus=new HashMap<>();
        voteArgus.put("limit",String.valueOf(limit));
        voteArgus.put("publisher",getCurrentUsername());
        voteArgus.put("publishedTime",String.valueOf(System.currentTimeMillis()));
        voteArgus.put("title",title);
        voteArgus.put("anonymous", String.valueOf(anonymous));
        redisUtil.hPutAll("class:" + classID + ":" + "type:" + "Vote"+":"+"title:"+title,voteArgus);
        for(String x:selections){
            redisUtil.zAdd("class:" + classID + ":" + "type:" + "VoteSelections"+":"+"title:"+title,x,0);
        }
        voteService.newVote(new Vote(0,classID,title,limit,selections,LocalDateTime.ofInstant(Instant.parse(voteArgus.get("publishedTime")),TimeZone.getDefault().toZoneId()),anonymous));
        //redisUtil.sAdd("class:" + classID + ":" + "type:" + "Voter"+":"+"title:"+title,"No more persons...");
        //请勿添加过多选项，以免响应缓慢
        return Result.success("投票已经开始");
    }
    @ApiOperation(value = "发布公告")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")//和下面保持一致
    @PostMapping(value = "/{classID}/publishNotice", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(name="minTimes",value = "超过这个时间后自动提醒未完成的学生")
    })
    public Result publishNotice(@PathVariable("classID") String classID, Notice notice,@RequestParam(required = false,defaultValue = "0") int minTimes) {
        if(minTimes!=0&&minTimes<179){
            return Result.error("提醒时间不宜小于三分钟，请设置的长一些。");
        }
        notice.setClassID(classID);
        notice.setPublishedTime(LocalDateTime.now());
        notice.setPublisher(getCurrentUsername());
        noticeService.newNotice(notice);
        if(minTimes!=0) {
            redisUtil.set("scheduledTask:" + "noticeUrge:" + "classID:" + classID + ":" + "nid" + ":" + notice.getNid(), classID, minTimes + random.nextInt(10));
        }
        //待加入的暂时存放于数据库中，然后传入时比对是否有待加入存在，否则无法加入
        //Redis
        return Result.success("公告已发布");
    }
    @ApiOperation(value = "催还没读公告的")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")//和下面保持一致
    @PostMapping(value = "/{classID}/notice/{nid}/urge", produces = "application/json;charset=UTF-8")
    public Result urgeNotice(@PathVariable("nid") int nid,@PathVariable("classID")String classID) {
        String classID2 = noticeService.getClassID(nid);
        Set<String> notConfirmed = redisUtil.sDifference("class:" + classID2 + ":type:" + "noticeConfirmed" + ":" + "nid:" + nid, "class:" + classID + ":" + "type:" + "members");
        noticeService.urge(notConfirmed);
        return Result.success("在催了在催了");
    }
    @ApiOperation(value = "催还没投票的的")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")//{title}待持久化到数据库后，会以ID取代之
    @PostMapping(value = "/{classID}/vote/{title}/urge", produces = "application/json;charset=UTF-8")
    public Result urgeVote(@PathVariable("title") String title,@PathVariable("classID")String classID) {
        Set<String> notConfirmed = redisUtil.sDifference("class:" + classID + ":" + "type:" + "Voter"+":"+"title:"+title, "class:" + classID + ":" + "type:" + "members");
        mailService.urgeStu(notConfirmed,"快去投票！");
        return Result.success("在催了在催了");
    }
    @ApiOperation("随机选人")
    @ApiImplicitParams({
            @ApiImplicitParam(name="num",value = "选取的人数")
    }
    )
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/RandomStu", produces = "application/json;charset=UTF-8")
    public Result RandomStu(@PathVariable("classID") String classID,@RequestParam(value = "num",defaultValue = "1")int num) {
        List<String> strings = redisUtil.sRandomMembers("class:" + classID +":"+"type:" + "members", num);
        //随机选人结果应该所有人都可见，否则具有不公平性
        redisUtil.sAdd("class:" + classID +":"+"type:" + "RandomSelect",strings.toArray(new String[0]));
        redisUtil.expire("class:" + classID +":"+"type:" + "RandomSelect",24*60*60,TimeUnit.SECONDS);
        //建议有多次选人，直接截图吧.....
        //其实本来这东西应该直接Push到客户端的，服务器不该保存到Redis中的......搞的现在前端得不断轮询服务器。。。。。
        return Result.success("已抽取",strings);
    }
    @ApiOperation(value = "加活跃分",notes = "活跃分上限为100，超过自动记为100分")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/addStuPoints", produces = "application/json;charset=UTF-8")
    public Result addStuPoints(@PathVariable("classID") String classID,@RequestParam("students")Set<String> students,int points) {
        if(points<-100||points>100){
            return Result.error("分数超出范围！");
        }
        studentPointsService.addStusPoints(students,classID,points);
        return Result.success("已加分");
    }
    @ApiOperation(value = "发布签到")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/publishSignIn", produces = "application/json;charset=UTF-8")
    public Result publishSignIn(@PathVariable("classID") String classID,String key) {
        Set<String> memSet=redisUtil.sGetMembers("class:" + classID +":"+"type:" + "members");
        String[] members=memSet.toArray(new String[0]);
        redisUtil.sAdd("class:" + classID + ":type:" + "signIn"+":"+"key:"+key,members);
        redisUtil.expire("class:" + classID + ":type:" + "signIn"+":"+"key:"+key,10*60, TimeUnit.SECONDS);
        return Result.success("签到已经发布了");
    }
    @ApiOperation(value = "获取已经签到的学生")
    @PreAuthorize("hasAnyAuthority('teacher_'+#classID)")
    @PostMapping(value = "/{classID}/getSignInDetail", produces = "application/json;charset=UTF-8")
    public Result checkSignIn(@PathVariable("classID") String classID,String key) {
        Set<String> notSingInList = redisUtil.sGetMembers("class:" + classID + ":type:" + "signIn" + ":" + "key:" + key);
        return Result.success("签到的学生",notSingInList);
    }
    //老师可以以xlsx导出学生成绩
}
