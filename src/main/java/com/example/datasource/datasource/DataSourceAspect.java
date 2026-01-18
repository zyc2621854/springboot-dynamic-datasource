package com.example.datasource.datasource;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据源切换切面
 * 拦截带有 @DataSource 注解的方法,动态切换数据源
 */
@Slf4j
@Aspect
@Component
@Order(1) // 确保在事务切面之前执行
public class DataSourceAspect {

    /**
     * 切点: 拦截带有 @DataSource 注解的方法
     */
    @Pointcut("@annotation(com.example.datasource.datasource.DataSource)")
    public void dataSourcePointCut() {
    }

    /**
     * 环绕通知: 在方法执行前后处理数据源切换
     */
    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        DataSource dataSource = method.getAnnotation(DataSource.class);
        if (dataSource == null) {
            return joinPoint.proceed();
        }

        DataSourceType dataSourceType = dataSource.value();
        log.info("切换数据源: {}", dataSourceType.getName());

        // 设置数据源类型
        DataSourceContextHolder.setDataSource(dataSourceType);

        try {
            // 执行目标方法
            return joinPoint.proceed();
        } finally {
            // 清理数据源类型
            DataSourceContextHolder.clearDataSource();
            log.info("清理数据源上下文");
        }
    }
}