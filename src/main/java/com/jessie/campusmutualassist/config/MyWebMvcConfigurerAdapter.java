package com.jessie.campusmutualassist.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfigurerAdapter implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //需要配置1：----------- 需要告知系统，这是要被当成静态文件的！
        //第一个方法设置访问路径前缀，第二个方法设置资源路径
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");

        //为了加个备案图片搞的这么复杂，象拔蚌玩意，网上一堆springboot1.5.x的教程搬来搬去的 2021年还在copy我是真的服了
        //一写代码就提示被弃用，时效性是真的不行啊

    }


}
