package ru.mai.trainticketsalesapp.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServiceAspect {

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void isServiceLayer() {
    }

    @Pointcut("isServiceLayer() && execution(public * *(..))")
    public void anyServicePublicMethod() {
    }

    @Before("anyServicePublicMethod()")
    public void addLogging(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        log.info("Service method called: {}.{}", className, methodName);
    }
}
