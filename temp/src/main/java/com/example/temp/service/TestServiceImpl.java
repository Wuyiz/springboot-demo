package com.example.temp.service;

import cn.hutool.core.thread.RejectPolicy;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TestServiceImpl implements TestService {
    private final ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100),
            ThreadUtil.createThreadFactory("test-pool-"),
            RejectPolicy.BLOCK.getValue());
    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();
    private static final InheritableThreadLocal<String> INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();


    @Async
    @Override
    public void asyncMethod() {
        log.info("asyncMethod - {}", Thread.currentThread().getName());
        int i = 1 / 0;
    }

    @Override
    public void testThreadLocalAndPool() {
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        INHERITABLE_THREAD_LOCAL.set("content");
        for (int i = 0; i < 100; i++) {
            final String content = "id-" + i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                THREAD_LOCAL.set(content);
                try {
                    // Thread.sleep(1000);
                    // log.info("isEqual={} content={} threadLocal={}", Objects.equals(content, THREAD_LOCAL.get()), content, THREAD_LOCAL.get());
                    log.info("isEqual={} content={} inheritableThreadLocal={}", Objects.equals(content, INHERITABLE_THREAD_LOCAL.get()), content, INHERITABLE_THREAD_LOCAL.get());
                    testService1.asyncMethod11(content);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    THREAD_LOCAL.remove();
                    // INHERITABLE_THREAD_LOCAL.remove();
                }
            }, poolExecutor);
            futureList.add(future);
        }
        // CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
    }

    @Resource
    private TestService1 testService1;

    @Service
    public static class TestService1 {
        @Async
        public void asyncMethod11(String content) {
            // log.info("isEqual={} content={} threadLocal={} 11", Objects.equals(content, THREAD_LOCAL.get()), content, THREAD_LOCAL.get());
            log.info("isEqual={} content={} inheritableThreadLocal={} 11", Objects.equals(content, INHERITABLE_THREAD_LOCAL.get()), content, INHERITABLE_THREAD_LOCAL.get());
        }
    }
}
