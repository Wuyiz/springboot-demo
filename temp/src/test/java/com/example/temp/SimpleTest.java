package com.example.temp;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

@Slf4j
class SimpleTest {

    @SneakyThrows
    @Test
    void name() {
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
}
