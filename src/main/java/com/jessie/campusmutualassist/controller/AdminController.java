package com.jessie.campusmutualassist.controller;

import com.jessie.campusmutualassist.entity.Result;
import com.jessie.campusmutualassist.service.StudentPointsService;
import com.jessie.campusmutualassist.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "管理员（还没东西）")
@RestController
public class AdminController {
    //管理员封人的时候，使用乐观锁
    //如果多个管理员同时封禁一个用户，那么可能会留下多个操作记录，但实际上只有一个人执行了这个操作
    //虽然这个相比买商品时多个用户同时购买了同一件商品没那么恶劣的后果，但是还是要注意一下
    @Autowired
    UserService userService;
    @Autowired
    StudentPointsService studentPointsService;

    @PostMapping(value = "/banUser", produces = "application/json;charset=UTF-8")
    public Result banUser(String targetUser, @RequestParam(defaultValue = "0") int status, String reason) {
        userService.setStatus(targetUser, status);
        return Result.success("封号成功");//话说这真的可以封号吗？？
    }

    @PostMapping(value = "/clearAllScores", produces = "application/json;charset=UTF-8")
    public Result banUser(String password) {
        //userService.setStatus(username,status);
        return Result.success("封号成功");//话说这真的可以封号吗？？
    }
}
