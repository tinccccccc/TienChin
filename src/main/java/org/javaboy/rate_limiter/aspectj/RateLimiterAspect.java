package org.javaboy.rate_limiter.aspectj;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.javaboy.rate_limiter.annotation.RateLimiter;
import org.javaboy.rate_limiter.emums.LimitType;
import org.javaboy.rate_limiter.exception.RateLimitException;
import org.javaboy.rate_limiter.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;

@Aspect
@Component
public class RateLimiterAspect {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterAspect.class);

    @Resource
    private RedisTemplate<Object,Object> redisTemplate;

    @Resource
    private RedisScript redisScript;

    @Before("@annotation(rateLimiter)")
    public void before(JoinPoint jp, RateLimiter rateLimiter) throws RateLimitException {
        int time = rateLimiter.time();
        int count = rateLimiter.count();
        String combinKey = getCombineKey(rateLimiter,jp);
        try {
            Long number = (Long) redisTemplate.execute(redisScript, Collections.singletonList(combinKey), time, count);
            if (number == null || number.intValue() > count){
                //超过限流阈值
                logger.info("当前接口已达到最大限流次数");
                throw new RateLimitException("访问过于频繁，请稍后访问");
            }
            logger.info("{}s 时间窗内请求次数：{}，当前请求次数：{}，缓存的key为：{}",time,count,number,combinKey);
        }catch (Exception e){
           throw e;
        }

    }

    /**
     * 这个key 其实就是接口调用次数缓存在 redis 的key
     * rate_limit:11.11.11.11-org.javaboy.ratelimit.controller.HelloController-hello
     *
     * @param rateLimiter
     * @param jp
     * @return
     */
    private String getCombineKey(RateLimiter rateLimiter, JoinPoint jp) {
        StringBuffer key = new StringBuffer(rateLimiter.key());
        if (rateLimiter.limitType() == LimitType.IP){
            key.append(IpUtils.getIpAddr((HttpServletRequest) RequestContextHolder.getRequestAttributes()))
                    .append("-");
        }
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        key.append(method.getDeclaringClass())
                .append("-")
                .append(method.getName());
        return key.toString();
    }
}
