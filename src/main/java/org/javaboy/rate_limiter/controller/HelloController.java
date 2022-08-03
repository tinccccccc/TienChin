package org.javaboy.rate_limiter.controller;

import org.javaboy.rate_limiter.annotation.RateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    /**
     * 10s  之内这个接口可以访问3次
     *
     * @return
     */
    @GetMapping("/hello")
    @RateLimiter(time=10,count = 3)
    public String hello(){
        return "hello";
    }

}
