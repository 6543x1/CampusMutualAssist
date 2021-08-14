package com.jessie.campusmutualassist.service;

import java.util.List;

public interface PermissionService
{




    void setUserPermission(String username, String permission);

    boolean queryUserPermission(String username,String permission);//注意重复数据好像会导致false

    List<String> getAllUserPermissions(String username);//和All搭配是不是要加s啊。。。。我英语不太好

    void deletePermission(String username,String permission);

}
