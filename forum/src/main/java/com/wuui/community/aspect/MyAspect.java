package com.wuui.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author Dee
 * @create 2022-05-20-21:38
 * @describe 通过aspectj实现AOP
 */
//@Component
//@Aspect
public class MyAspect {

    //service包下的所有方法（所有参数及返回值）
    @Pointcut("execution(* com.wuui.community.service.*.*(..))")
    public void pointcut(){

    }

    @Before("pointcut()")
    public void before(){
        System.out.println("==========before");
    }

    @After("pointcut()")
    public void after(){
        System.out.println("==========after");
    }

    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("==========AfterReturning");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("==========AfterThrowing");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("==========beforeAround");
        Object obj = joinPoint.proceed();
        System.out.println("==========afterAround");
        return obj;
    }

}
