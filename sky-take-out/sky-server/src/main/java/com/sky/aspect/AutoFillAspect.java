package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;  // 修复：使用 aspectj 的 MethodSignature
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("进行公共字段数据填充");
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof Employee) {
                    Employee employee = (Employee) arg;
                    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                    AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
                    if (annotation != null) {
                        switch (annotation.value()) {
                            case INSERT:
                                employee.setCreateTime(LocalDateTime.now());
                                employee.setCreateUser(BaseContext.getCurrentId());
                                break;
                            case UPDATE:
                                employee.setUpdateTime(LocalDateTime.now());
                                employee.setUpdateUser(BaseContext.getCurrentId());
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }

    }
}
