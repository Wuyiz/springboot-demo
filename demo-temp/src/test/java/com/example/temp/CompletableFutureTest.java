package com.example.temp;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CompletableFutureTest {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        TimeInterval timer = DateUtil.timer();
        log.info("开始......{}.........", timer.interval());
        List<String> list = Collections.synchronizedList(new LinkedList<>());
        CompletableFuture<String> future1 = CompletableFuture
                .supplyAsync(() -> fuc(1, 2000L), executor)
                .thenApply(res -> {
                    log.info("{}: {}  {} ms", res, Thread.currentThread().getName(), timer.interval());
                    return res;
                }).whenComplete((res, ex) -> {
                    list.add(res);
                    log.info("{}_list: {} ， {}", res, list, Thread.currentThread().isDaemon());
                });
        CompletableFuture<String> future2 = CompletableFuture
                .supplyAsync(() -> fuc(2, 2000L))
                .thenApply(res -> {
                    log.info("{}: {}  {} ms", res, Thread.currentThread().getName(), timer.interval());
                    return res;
                }).whenComplete((res, ex) -> {
                    list.add(res);
                    log.info("{}_list: {} ， {}", res, list, Thread.currentThread().isDaemon());
                });
        CompletableFuture<String> future3 = CompletableFuture
                .supplyAsync(() -> fuc(3, 3000L))
                .thenApply(res -> {
                    log.info("{}: {}  {} ms", res, Thread.currentThread().getName(), timer.interval());
                    return res;
                }).whenComplete((res, ex) -> {
                    list.add(res);
                    log.info("{}_list: {} ， {}", res, list, Thread.currentThread().isDaemon());
                });
        log.info("list_main: {} ， {}", list, Thread.currentThread().isDaemon());

        // String result_1 = future1.join();
        // log.info("{} : {}", result_1, timer.interval());
        //
        // String result_2 = future2.join();
        // log.info("{} : {}", result_2, timer.interval());

        // String result_3 = future3.join();
        // log.info("{} : {}", result_3, timer.interval());

        log.info("结束......{}ms.........", timer.interval());
        executor.shutdown();
    }

    public static String fuc(int order, Long sec) {
        log.info("getPay=============");
        try {
            Thread.sleep(sec);
            // Awaitility.await().atMost(5,TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("error: ", e);
        }
        log.info("fuc_{}，睡眠时间: {}ms === {}：{}", order, sec, Thread.currentThread().getName(), Thread.currentThread().isDaemon());
        // 返回支付信息
        return "fuc_" + order;
    }
}
