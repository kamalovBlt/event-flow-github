package com.technokratos.aspects;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
@Slf4j
public class LoggingAnnotationAspect {
    @Around("@annotation(com.technokratos.annotation.Logging)")
    public Object logMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.debug("Invoking @Logging class {} method {} with args: {}", className, method, args);

        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception in @Logging class {} method {}: {}", className, method, e.getMessage(), e);
            throw new RuntimeException(e);
        }

        log.debug("@Logging class {} method {} returned: {}", className, method, result);
        return result;
    }

}
