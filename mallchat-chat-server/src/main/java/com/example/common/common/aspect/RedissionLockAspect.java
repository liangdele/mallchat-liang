package com.example.common.common.aspect;

import com.example.common.common.annotation.RedissonLock;
import com.example.common.common.service.LockService;
import com.example.common.common.utils.SpElUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
@Order(0)//确保比事物注解先执行
public class RedissionLockAspect {
    @Autowired
    private LockService lockService;

    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String prefix = StringUtils.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method) : redissonLock.prefixKey();
        String key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), redissonLock.key());
        return lockService.executeWithLock(prefix + ":" + key, redissonLock.waitTime(), redissonLock.unit(), joinPoint::proceed);
    }
}
