package com.example.temp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestServiceImpl implements TestService {

    @Async
    @Override
    public void asyncMethod() {
        log.info("asyncMethod - {}", Thread.currentThread().getName());
        int i = 1 / 0;
    }
}
