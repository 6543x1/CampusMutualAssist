package com.jessie.campusmutualassist.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.jessie.campusmutualassist.entity.SimpleStu;
import com.jessie.campusmutualassist.mapper.PermissionMapper;
import com.jessie.campusmutualassist.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service("permissionService")
public class PermissionServiceImpl implements PermissionService
{
    @Autowired
    private PermissionMapper permissionMapper;


    @Override
    @CacheEvict(value = "userPermissions",key ="#username+'*'" )
    public void setUserPermission(String username,String permission)
    {
        permissionMapper.setUserPermission(username,permission);
    }

    @Override
    @Cacheable(value = "userPermissions",key ="#username+':'+#permission" )
    public boolean queryUserPermission(String username,String permission)
    {
        return permissionMapper.queryUserPermission(username,permission);
    }

    @Override
    @Cacheable(value = "userPermissions",key ="#username" )
    public List<String> getAllUserPermissions(String username)
    {
        return permissionMapper.getAllUserPermissions(username);
    }
    public static String getCurrentUsername(){

        String username;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal == null)
        {
            return null;
        }
        if (principal instanceof UserDetails)
        {
            UserDetails userDetails = (UserDetails) principal;
            username = userDetails.getUsername();
        } else
        {
            username = principal.toString();
        }
        return username;

    }
    public static List getCurrentPermission(){

        Collection<? extends GrantedAuthority> authorities;
        List<GrantedAuthority> list;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal == null)
        {
            return null;
        }
        if (principal instanceof UserDetails)
        {
            UserDetails userDetails = (UserDetails) principal;
            authorities=userDetails.getAuthorities();
            list=new ArrayList<>(authorities);
        } else
        {
            list=null;
        }
        return list;

    }

    @Override
    @CacheEvict(value = "userPermissions",key ="#username+'*'" )
    public void deleteUserPermission(String username, String permission) {
        permissionMapper.deletePermission(username,permission);
    }

    @Override
    @Async
    public void deleteUsersPermission(Set<String> usernames, String permission) {
        usernames.forEach((username)->deleteUserPermission(username,permission));//lambda还挺好写的，之前的就懒得改了吧
    }

    @Override
    @Cacheable(value = "classMembers",key="#classID")//这个不好删除，可以考虑设置过期时间短一些（？）
    public JSONObject getClassListSortByPermission(String classID){
        JSONObject jsonObject=new JSONObject();
        List<SimpleStu> list=permissionMapper.getClassMemByPermissions("teacher","teacher_"+classID);
        jsonObject.put("teacher",list);
        List<SimpleStu> monitorList=permissionMapper.getClassMemByPermissions("monitor","monitor_"+classID);
        jsonObject.put("monitor",monitorList);
        List<SimpleStu> assistantList=permissionMapper.getClassMemByPermissions("student_"+classID,"teacher_"+classID);
        jsonObject.put("assistant",assistantList);
        List<SimpleStu> stuList=permissionMapper.getClassMemByPermissions("student","student_"+classID);
        jsonObject.put("student",stuList);
        return jsonObject;
    }
}
