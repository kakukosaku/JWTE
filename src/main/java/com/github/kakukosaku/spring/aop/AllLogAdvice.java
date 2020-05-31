package com.github.kakukosaku.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/30
 */
public class AllLogAdvice {

    public void logBeforeAdvice(JoinPoint joinPoint) {
        List<Object> args = Arrays.asList(joinPoint.getArgs());

        String logInfoText = "before log: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        logInfoText += " " + args.get(0).toString() + " view " + args.get(1).toString();

        System.out.println(logInfoText);
    }

    public void logAfterReturnAdvice(JoinPoint joinPoint) {
        List<Object> args = Arrays.asList(joinPoint.getArgs());

        String logInfoText = "after log: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        logInfoText += " " + args.get(0).toString() + " view " + args.get(1).toString();

        System.out.println(logInfoText);
    }

    public void logAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        System.out.println("around log: spend time :" + (endTime - beginTime) + "ms");
    }

}
