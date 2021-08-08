package com.jessie.campusmutualassist.config;

import com.alibaba.fastjson.JSONObject;
import com.jessie.campusmutualassist.entity.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler
{
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException
    {
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(403);
//        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
        //上面那句话不要写....要不后面就全部木大了...
        PrintWriter pw = response.getWriter();
        pw.print(JSONObject.toJSON(Result.error("无权限被拒绝", 403)));
        pw.flush();
        pw.close();
        System.out.println("AccessDeniedHandler 生效");
    }
}
