package org.javaboy.rate_limiter.emums;

/**
 * 限流的类型
 */
public enum  LimitType {

    /**
     * 默认限流策略，针对某一个接口进行限流
     */
    DEFAULT,

    /**
     * 针对某一个IP进行限流
     */
    IP

}
