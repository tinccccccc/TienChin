package org.javaboy.rate_limiter.annotation;

import org.javaboy.rate_limiter.emums.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimiter {

    /**
     * 限流的 key，主要指前缀
     */
    String key() default "rate_limit:";

    /**
     * 限流时间窗
     */
    int time() default 60;

    /**
     * 时间窗内的限流次数
     */
    int count() default 100;

    /**
     * 限流类型
     */
    LimitType limitType() default LimitType.DEFAULT;
}
