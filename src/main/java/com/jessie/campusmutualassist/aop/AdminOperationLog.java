package com.jessie.campusmutualassist.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminOperationLog {
    String module() default ""; // 操作模块
}
