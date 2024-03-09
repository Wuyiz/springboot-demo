package com.example.temp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AsyncService {
    @Async
    public void asyncMethod() {
        log.info("@Async注解配置测试生效 - {}", Thread.currentThread().getName());
    }
}