package com.jessie.campusmutualassist.service.impl;


import com.jessie.campusmutualassist.mapper.PermissionMapper;
import com.jessie.campusmutualassist.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("permissionService")
public class PermissionServiceImpl implements PermissionService
{
    @Autowired
    private PermissionMapper permissionMapper;


    @Override
    public void setUserPermission(String username,String permission)
    {
        permissionMapper.setUserPermission(username,permission);
    }

    @Override
    public boolean queryUserPermission(String username,String permission)
    {
        return permissionMapper.queryUserPermission(username,permission);
    }

    @Override
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
}
