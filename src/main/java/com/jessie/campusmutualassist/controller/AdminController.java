package com.jessie.campusmutualassist.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    //管理员封人的时候，使用乐观锁
    //如果多个管理员同时封禁一个用户，那么可能会留下多个操作记录，但实际上只有一个人执行了这个操作
    //虽然这个相比买商品时多个用户同时购买了同一件商品没那么恶劣的后果，但是还是要注意一下
}
