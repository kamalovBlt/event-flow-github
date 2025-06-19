package com.technokratos.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.aop.framework.AopProxyUtils;

@Aspect
@Slf4j
public class ControllerServiceRepositoryLoggingAspect {

    @Around("@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.debug("Controller {} method {} ", className, methodName);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Controller {} method {} threw an exception: {}", className, methodName, e.getMessage(), e);
            throw e;
        }

        log.debug("Controller {} method {}", className, methodName);
        return result;
    }

    @Around("@within(org.springframework.stereotype.Service)")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.debug("Service {} method {}", className, methodName);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Service {} method {} threw an exception: {}",className, methodName, e.getMessage(), e);
            throw e;
        }

        log.debug("Service {} method {}",className, methodName);
        return result;
    }

    @Around("execution(* com.technokratos.repository..*(..)) || @within(org.springframework.stereotype.Repository)")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(joinPoint.getTarget());
        String className;
        className = targetClass.getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("Repository {} method {}", className, methodName);

        long start = System.currentTimeMillis();
        long duration;

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            duration = System.currentTimeMillis() - start;
            log.error("Repository {} method {} threw an exception after {} ms: {}", className, methodName, duration, e.getMessage(), e);
            throw e;
        }
        duration = System.currentTimeMillis() - start;
        log.warn("Repository {} method {}-Response after {}",className, methodName, duration);
        return result;
    }

}
