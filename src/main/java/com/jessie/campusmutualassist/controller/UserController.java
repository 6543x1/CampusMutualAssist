package com.jessie.campusmutualassist.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.AdminOperation;
import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.entity.User;
import com.jessie.campusmutualassist.entity.myEnum.Role;
import com.jessie.campusmutualassist.service.AdminOperationService;
import com.jessie.campusmutualassist.service.MailService;
import com.jessie.campusmutualassist.service.PermissionService;
import com.jessie.campusmutualassist.service.UserService;
import com.jessie.campusmutualassist.utils.JwtTokenUtil;
import com.jessie.campusmutualassist.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

import static com.jessie.campusmutualassist.service.impl.MailServiceImpl.getRandomString;
import static com.jessie.campusmutualassist.service.impl.PermissionServiceImpl.getCurrentUsername;

@Api(tags = "用户账号类操作")
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource(name = "theImgSuffix")
    HashMap<Integer, String> theImgSuffix;
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;//从MD5直接升级Bcrypt，真香
    @Autowired
    private MailService mailService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private AdminOperationService adminOperationService;//仅用于查询用户被管理员操作记录

    @ApiOperation(value = "修改密码")
    @PostMapping(value = "/ResetPw", produces = "application/json;charset=UTF-8")
    public Result editPassword(String oldPassword, String newPassword) throws Exception {

        if (userService.cmpPassword(getCurrentUsername(), oldPassword)) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            userService.editPassword(getCurrentUsername(), bCryptPasswordEncoder.encode(newPassword));
            return Result.success("修改成功");
        } else {
            return Result.error("原密码错误", 403);
        }
    }

    //Spring Security获取当前会话的用户信息

    @GetMapping(value = "/loginSuccess", produces = "application/json;charset=UTF-8")
    public Result isLogin() throws Exception {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        User user = userService.getUser(username);
        user.setPassword("加密也不给你看");
        String userInfo = JSON.toJSONString(user);
        System.out.println(userInfo);
        System.out.println(redisUtil.get("Jwt_TOKEN|" + getCurrentUsername()));
        Result<User> result = Result.success("loginSuccess", user);
        return result;
    }

    @ApiOperation(value = "注册", notes = "需要验证邮箱,这个方法不发送邮件, 发送邮件是/user/sendMail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名，请使用学号，不要带邮箱", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "String"),
            @ApiImplicitParam(name = "role", value = "账号角色，teacher/student/admin", dataType = "enum"),
            @ApiImplicitParam(name = "realName", value = "真实姓名", dataType = "String")
    })
    @PostMapping(value = "/register", produces = "application/json;charset=UTF-8")
    public Result RealRegister(User user) throws Exception {
        //说回来要是有人破解了链接给我服务器扔冲蝗核弹咋办 springboot有防御措施吗...
        if (user == null) {
            return Result.error("无数据");
        }
        System.out.println(user);
        if (user.getUsername().length() >= 20 || user.getPassword().length() >= 30) {
            return Result.error("账号或密码长度过长，请缩短");
        }
        System.out.println("取得注册用数据，开始向数据库中写入数据...");

        if (userService.queryUser(user.getUsername())) {
            System.out.println("该用户名已存在，请检查输入是否错误");
            return Result.error("该用户名已存在", 500);
        }
        if (user.getRole() == Role.admin || user.getRole() == Role.counsellor) {
            return Result.error("请不要通过注册来注册管理员或辅导员,管理员需要后台手动设置");
        }
        user.setStatus(1);//封号与否 就不枚举了吧？
        if (user.getRealName() == null || "".equals(user.getRealName())) {
            return Result.error("请填写真实姓名！");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));//换用Bcrypt，自动加盐真香
        user.setEvaluation(2);//目前没啥用
        String thisUsername = user.getUsername();
        if (thisUsername.matches("M[0-9]+") || thisUsername.matches("N[0-9]+")) {
            user.setRole(Role.student);//不好意思没设置研究生
        } else if (thisUsername.matches("[0-9]{9}")) {
            user.setRole(Role.student);
        } else if (thisUsername.matches("T[0-9]{5}") || thisUsername.matches("F[0-9]{5}")) {
            user.setRole(Role.teacher);
        } else {
            user.setRole(Role.student);
        }
        //备注一下，应该在Redis中留下痕迹，表明邮箱未经过认证，同时也不授予其stu或teacher的认证........
        //我觉得此时这个User不应该保存到数据库里的，应该先把全部内容放到Redis中?
        if (redisUtil.hasKey("type:" + "register:" + "username:" + user.getUsername())) {
            return Result.error("当前已经有相同的学号正在注册，请稍候再试！如果疑问联系客服");
        }
        redisUtil.setObject("type:" + "register:" + "username:" + user.getUsername(), user, 1800);

        System.out.println(user);
        return Result.success("请到邮箱接收验证码，完成后续注册操作。");
    }

    @ApiOperation(value = "/比较验证码是否正确")
    @PostMapping(value = "/compareCode", produces = "application/json;charset=UTF-8")
    public boolean compareCode(String username, String mailCode) {
        if (redisUtil.hasKey("MailCode:" + username)) {
            return mailCode.equals(redisUtil.get("MailCode:" + username));
        } else {
            return false;
        }
    }

    @ApiOperation(value = "完成注册", notes = "需要验证邮箱")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名，请使用学号，不要带邮箱", dataType = "String"),
            @ApiImplicitParam(name = "code", value = "邮箱中的验证码", dataType = "String"),
    })
    @PostMapping(value = "/finishRegister", produces = "application/json;charset=UTF-8")
    public Result finishRegister(String username, String mailCode) {
        //此处依然存在一个线程安全问题.....如果中途有人抢注?
        String theCode = redisUtil.get("MailCode:" + username);
        if (theCode == null) {
            return Result.error("没有信息！请重发邮件");
        }
        if (!theCode.equals(mailCode)) {
            return Result.error("请检查邮件验证码是否有误");
        }
        User user = redisUtil.getObject("type:" + "register:" + "username:" + username, User.class);
        if (user == null) {
            return Result.error("服务器信息已过期，请重新注册");
        }
        System.out.println(user);
        userService.saveUser(user);
        if (user.getRole() == Role.teacher) {
            permissionService.setUserPermission(user.getUsername(), Role.teacher.name());
        } else if (user.getRole() == Role.student) {
            permissionService.setUserPermission(user.getUsername(), Role.student.name());
        }
        return Result.success("注册成功");
    }

    @ApiOperation(value = "测试用快速注册", notes = "相比普通注册，不用验证邮箱即可注册成功，上线后会删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名，请使用学号，暂时不用附带邮箱"),
            @ApiImplicitParam(name = "password", value = "密码"),
            @ApiImplicitParam(name = "role", value = "账号角色，teacher/student/admin")
    })
    @PostMapping(value = "/quickRegister", produces = "application/json;charset=UTF-8")
    public Result register(User user) {
        //说回来要是有人破解了链接给我服务器扔冲蝗核弹咋办 springboot有防御措施吗...
        if (user == null) {
            return Result.error("无数据");
        }
        System.out.println(user);
        if (user.getUsername().length() >= 20 || user.getPassword().length() >= 30) {
            return Result.error("想扔内存核弹？");
        }
        System.out.println("取得注册用数据，开始向数据库中写入数据...");
        if (userService.getUser(user.getUsername()) != null) {
            System.out.println("用户名已存在，请重试");
            return Result.error("用户名已存在", 500);
        }
        user.setStatus(10);
        if (user.getRealName() == null || "".equals(user.getRealName())) {
            user.setRealName(user.getUsername());
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));//换用Bcrypt，自动加盐真香
        user.setEvaluation(2);
        //备注一下，应该在Redis中留下痕迹，表明邮箱未经过认证，同时也不授予其stu或teacher的认证........
        //我觉得此时这个User不应该保存到数据库里的，应该先把全部内容放到Redis中?
        userService.saveUser(user);
        if (user.getRole() == Role.teacher) {
            permissionService.setUserPermission(user.getUsername(), Role.teacher.name());
        } else if (user.getRole() == Role.student) {
            permissionService.setUserPermission(user.getUsername(), Role.student.name());
        } else {
            return Result.error("角色选择错误，请正确选择你的角色");
        }
        //int theUid = userService.newestUid();
        //userTokenService.newUser(user.getUid(), user.getUsername());
        //UserPortrait userPortrait = new UserPortrait(user.getUid());
        //userService.newUserPortrait(userPortrait);
        //redisUtil.set("ClearExtraStatus|" + userPortrait.getUid(), "ONE YEAR", 60 * 60 * 24 * 365);
        return Result.success("注册成功");
    }

    @ApiOperation(value = "检验管理员身份（目前没啥用）")
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping(value = "/checkAdmin", produces = "application/json;charset=UTF-8")
    public String IAMAdmin() throws Exception {
        return JSON.toJSONString(Result.success("你是管理员"));
    }

    @ApiOperation(value = "设置邮箱（不同于福大邮箱）", notes = "暂时先别用")
    @PostMapping(value = "/setMailAddr", produces = "application/json;charset=UTF-8")
    public String setMailAddress(String mailAddr) throws Exception {
        String username = getCurrentUsername();
        if (username == null) {
            return JSON.toJSONString(Result.error("找不到用户名(服务器出错联系管理员)"));
        }
        if (mailAddr == null) {
            return JSON.toJSONString(Result.error("邮箱是空的"));
        }
        if (userService.getUser(username).getMailAddr() != null) {
            return JSON.toJSONString(Result.error("已经设置邮箱了，如果失效请联系管理员更改"));
        }
        String theCode = getRandomString();
        mailService.sendResetPw(mailAddr, username + "的请求码(六位字符）:" + theCode);
        return JSON.toJSONString(Result.success("SuccessPlzConfirm"));
    }

    @PostMapping(value = "/confirmAddr", produces = "text/html;charset=UTF-8")
    public String confirmAddr(String mailCode) throws Exception {
        Result res;
        String username = getCurrentUsername();
        if (username == null) {
            return JSON.toJSONString("EMPTY USERNAME");
        }
        String trueCode = "userTokenService.getMailCode(username)";
        if (trueCode.equals(mailCode)) {
            //userService.setMailAddr(jwtTokenUtil.getUidFromToken(token), userTokenService.getTemp(username));//如果mailCode未清空则后续无法设置成功会被清掉
            res = Result.success("confirmSuccess");
            //userTokenService.saveMailCode(username, "", "");
        } else {
            res = Result.error("Incorrect code", 400);
        }
        return JSON.toJSONString(res);
    }

    @ApiOperation(value = "发送验证码到邮箱，注册和找回密码时使用", notes = "注册和找回密码时使用,默认发送到username@fzu.edu.cn")
    @PostMapping(value = "/sendMail", produces = "application/json;charset=UTF-8")
    public Result SendPwMail(HttpServletRequest request, String username) throws Exception {
        if (username == null) {
            return Result.error("找不到用户名(服务器出错联系管理员)");
        }
        String mailAddr;
        String theCode = getRandomString();
        mailAddr = username + "@fzu.edu.cn";
        if (!redisUtil.hasKey("type:" + "register:" + "username:" + username) || !userService.queryUser(username)) {
            return Result.error("当前用户不存在或是已经被注册!");
        }
        System.out.println(username);
        if (redisUtil.hasKey("MailCodeTimes:" + username)) {
            int num = Integer.parseInt(redisUtil.get("MailCodeTimes:" + username));
            if (num <= 3) {

                redisUtil.incrBy("MailCodeTimes:" + username, 1);
            } else {
                return Result.error("发过三次了，如果没收到联系管理员", 404);
            }
            //userTokenService.getMailCode(username) != null && ("2").equals(userTokenService.getTemp(username))
            //return JSON.toJSONString(Result.success("已经发送了，请到邮箱查看邮件"));
        } else {
            redisUtil.set("MailCodeTimes:" + username, "1", 2 * 60 * 60);
        }
        redisUtil.set("MailCode:" + username, theCode);
        mailService.newMessage("您在jessie back上的验证码", mailAddr, username + "的请求码(六位字符）:" + theCode);
        System.out.println(theCode);
        return Result.success("请到邮箱查看邮件");
    }

    @ApiOperation(value = "拿到验证码后重置密码")
    @PostMapping(value = "/ResetPwByMail", produces = "text/html;charset=UTF-8")
    public String editPasswordByMail(HttpServletRequest request, String mailCode, String username, String newPassword) throws Exception {
        Result res;
        if (username == null) {
            return JSON.toJSONString("EMPTY USERNAME");
        }
        try {


//            ArrayList<String> data = redisUtil.get("MailCode|" + username, ArrayList.class);
            String trueCode = redisUtil.get("MailCode:" + username);
            if (trueCode.equals(mailCode)) {
                userService.editPassword(username, bCryptPasswordEncoder.encode(newPassword));
                //userTokenService.saveMailCode(username, "", "");
                //我为什么要这么脑残的去用UID呢？？？？？？？？？？？？？？？？？？？？？？？？
                res = Result.success("confirmSuccess");
                redisUtil.delete("MailCode:" + username);
            } else {
                res = Result.error("Incorrect code", 400);
            }
        } catch (NullPointerException e) {
            res = Result.error("似乎验证码过期了或者不存在,请重新发送邮件试试", 404);
        }
        return JSON.toJSONString(res);
    }

    @ApiOperation(value = "返回当前用户名")
    @PostMapping(value = "/isLogin", produces = "application/json;charset=UTF-8")
    public Result loginIN() throws Exception {

        try {
            return Result.success("是" + getCurrentUsername(), 200);
        } catch (NullPointerException e) {
            return Result.error("未检测到登录用户的信息");
        }
    }


    @PostMapping(value = "/getAdminHistory", produces = "application/json;charset=UTF-8")
    public PageInfo<AdminOperation> getUserAllOperations(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, HttpServletRequest request) {
        String token = request.getHeader("token");
        int uid = jwtTokenUtil.getUidFromToken(token);
        PageHelper.startPage(pageNum, 10);
        List<AdminOperation> list = adminOperationService.getAllOperations(uid);
        //pageHelper会直接操作下一步的mysql操作来进行分页,所以只要持久化到mysql中的数据都能分页吧
        //开启Cache后还能分页吗？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
        return new PageInfo<AdminOperation>(list);
    }
}
