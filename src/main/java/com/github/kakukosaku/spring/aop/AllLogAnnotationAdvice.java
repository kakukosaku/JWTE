package com.github.kakukosaku.spring.aop;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/30
 */
@Aspect
@Component
public class AllLogAnnotationAdvice {

    @Pointcut("execution(* com.github.kakukosaku.spring..*.* (..))")
    public void allMethod() {
    }

    @Before("allMethod()")
    public void logBeforeAdvice(JoinPoint joinPoint) {
        List<Object> args = Arrays.asList(joinPoint.getArgs());

        String logInfoText = "before log: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        logInfoText += " " + args.get(0).toString() + " view " + args.get(1).toString();

        System.out.println(logInfoText);
    }

    @AfterReturning("allMethod()")
    public void logAfterReturnAdvice(JoinPoint joinPoint) {
        List<Object> args = Arrays.asList(joinPoint.getArgs());

        String logInfoText = "after log: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        logInfoText += " " + args.get(0).toString() + " view " + args.get(1).toString();

        System.out.println(logInfoText);
    }

    @Around("allMethod()")
    public void logAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        System.out.println("around log: spend time :" + (endTime - beginTime) + "ms");
    }
}
