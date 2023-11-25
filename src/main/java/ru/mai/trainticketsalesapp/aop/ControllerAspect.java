package ru.mai.trainticketsalesapp.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ControllerAspect {

    @Pointcut("@within(org.springframework.stereotype.Controller)")
    public void isControllerLayer() {
    }


}
