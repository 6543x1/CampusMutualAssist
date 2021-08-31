package com.jessie.campusmutualassist.aop.aspect;

import com.jessie.campusmutualassist.aop.PointsOperationLog;
import com.jessie.campusmutualassist.entity.StuPointsDetail;
import com.jessie.campusmutualassist.service.StuPointsDetailService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class PointsOperationLogAspect {

    @Autowired
    StuPointsDetailService stuPointsDetailService;

    @Pointcut("@annotation(com.jessie.campusmutualassist.aop.PointsOperationLog)")
    public void pointcut() {
    }

    @AfterReturning(returning = "returnOb", pointcut = "pointcut()")
    public void doAfterReturning(JoinPoint joinPoint, Object returnOb) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();
        PointsOperationLog annotation = method.getAnnotation(PointsOperationLog.class);
        if (annotation != null) {
            log.info(annotation.module());
            log.info(annotation.type());
            log.info(annotation.desc());
        }
        log.info("################ServiceMethod : + addStuPointDetail....");
        // 记录下请求内容
//        log.info("################URL : " + request.getRequestURL().toString());
//        log.info("################HTTP_METHOD : " + request.getMethod());
//        log.info("################IP : " + request.getRemoteAddr());
//        log.info("################THE ARGS OF THE CONTROLLER : " + Arrays.toString(joinPoint.getArgs()));

        //下面这个getSignature().getDeclaringTypeName()是获取包+类名的   然后后面的joinPoint.getSignature.getName()获取了方法名
        //log.info("################CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        //log.info("################TARGET: " + joinPoint.getTarget());//返回的是需要加强的目标类的对象
        //log.info("################THIS: " + joinPoint.getThis());//返回的是经过加强后的代理类的对象
        //System.out.println("##################### the return of the method is : " + returnOb);
        Object[] args = joinPoint.getArgs();
//        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
//        String[] paramNames = u.getParameterNames(method);
        String[] paramNames = signature.getParameterNames();
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (args != null && paramNames != null) {
            String params = "";
            for (int i = 0; i < args.length; i++) {
                params += "  " + paramNames[i] + ": " + args[i];
                map.put(paramNames[i], args[i]);
            }
            System.out.println(params);
        }
        StuPointsDetail stuPointsDetail = new StuPointsDetail();
        if("缺勤扣分".equals(annotation.module())){
            stuPointsDetail.setPoints(-3);
        }
        else{
        stuPointsDetail.setPoints((Integer) map.get("points"));}
        stuPointsDetail.setClassID((String) map.get("classID"));
        stuPointsDetail.setOperator((String) map.get("operator"));
        stuPointsDetail.setReason((String) map.get("reason"));
        stuPointsDetail.setId(0);
        if (map.containsKey("student")) {
            stuPointsDetail.setTarget((String) map.get("student"));
            //注意一下 那个批量加分的里面是students}
            stuPointsDetailService.newDetail(stuPointsDetail, Collections.singleton((String) map.get("student")));
            //System.out.println(stuPointsDetail);
        } else if (map.containsKey("students")) {
            Set<String> set = (Set<String>) map.get("students");//别慌，强转就完事了
            stuPointsDetailService.newDetail(stuPointsDetail, set);
        }
    }

}
