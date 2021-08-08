package com.jessie.campusmutualassist.config;

import com.alibaba.fastjson.JSONObject;
import com.jessie.campusmutualassist.entity.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable
{
    private static final long serialVersionUID = 1L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException
    {
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        PrintWriter pw = response.getWriter();
        pw.print(JSONObject.toJSON(Result.error("未登录被拒绝", 401)));
        pw.flush();
        pw.close();
    }
}
