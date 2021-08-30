package com.jessie.campusmutualassist.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Set;

public interface PermissionService {


    void setUserPermission(String username, String permission);

    boolean queryUserPermission(String username, String permission);//注意重复数据好像会导致false

    List<String> getAllUserPermissions(String username);//和All搭配是不是要加s啊。。。。我英语不太好

    void deleteUserPermission(String username, String permission);

    void deleteUsersPermission(Set<String> usernames, String permission);

    public JSONObject getClassListSortByPermission(String classID);
}
