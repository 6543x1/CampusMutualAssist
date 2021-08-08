package com.jessie.campusmutualassist.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class XSSFilter extends OncePerRequestFilter
{
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException
    {
        Logger logger = LoggerFactory.getLogger(XSSFilter.class);
        logger.info("XSS FILTER running");
        filterChain.doFilter(new XssHttpServletRequestWrapper(httpServletRequest), httpServletResponse);
    }
}
