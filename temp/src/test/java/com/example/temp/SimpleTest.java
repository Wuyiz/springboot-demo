package com.example.temp;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
class SimpleTest {

    @SneakyThrows
    @Test
    void ipHostTest() {
        String hostAddress = Inet4Address.getLocalHost().getHostAddress();
        System.out.println(hostAddress);
        System.out.println(StrUtil.subAfter("Label.Data.Screen.User.233", StrUtil.DOT, true));
    }

    @Test
    void javaLocalDateTimeTest() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        int monthValue = now.getMonthValue();
        Month month = now.getMonth();
        // 不改变时间，只是在末尾追加时区偏移量
        OffsetDateTime offsetDateTime = now.atOffset(ZoneOffset.of("+01:00"));
        LocalDateTime truncatedTo = now.truncatedTo(ChronoUnit.HALF_DAYS);

        System.out.println("now：" + now);
        System.out.println("firstDayOfMonth：" + firstDayOfMonth);
        System.out.println("monthValue：" + monthValue);
        System.out.println("month：" + month);
        System.out.println("offsetDateTime：" + offsetDateTime);
        System.out.println("truncatedTo：" + truncatedTo);
    }

    @Test
    void name() {
        log.info("System::currentTimeMillis {}", System.currentTimeMillis());
        log.info("SystemClock::now {}", SystemClock.now());
    }

    @Test
    void systemTimeMillisTest() {
        int core = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor threadPoolExecutor = ThreadUtil.newExecutor(core + 1, core + 1);
        int loop = 1_000_000;
        TimeInterval timer = DateUtil.timer();

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < loop; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(System::currentTimeMillis);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        log.info("System::currentTimeMillis {}ms", timer.intervalRestart());


        List<CompletableFuture<Void>> futureList2 = new ArrayList<>();
        for (int i = 0; i < loop; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(SystemClock::now);
            futureList2.add(future);
        }
        CompletableFuture.allOf(futureList2.toArray(new CompletableFuture[0])).join();
        log.info("SystemClock::now {}ms", timer.intervalRestart());
    }
}
