package com.example.common.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedissonLock {
    /**
     * key的前缀，默认取方法全限定名，可以自己指定
     * @return
     */
    String prefixKey() default "";

    /**
     * spring el表达式，可以从方法参数中取值
     * @return
     */
    String key() ;

    /**
     * 等待锁的排队时间，默认不等待快速失败
     * @return
     */
    int waitTime() default -1;

    /**
     * 锁时间单位，默认毫秒
     * @return
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
