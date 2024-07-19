package com.example.common.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

/**
 * 日期时间工具类
 *
 * @author suhai
 * @since 2024-07-01
 */
public class DateTimeUtils {
    public static final String CHRONOS_UNIT_STR_SECONDS = "seconds";
    public static final String CHRONOS_UNIT_STR_MINUTES = "minutes";
    public static final String CHRONOS_UNIT_STR_HOURS = "hours";
    public static final String CHRONOS_UNIT_STR_DAYS = "days";
    public static final String CHRONOS_UNIT_STR_WEEKS = "weeks";
    public static final String CHRONOS_UNIT_STR_MONTHS = "months";
    public static final String CHRONOS_UNIT_STR_YEARS = "years";

    /**
     * 时间单位映射池（key不区分大小写）
     */
    private static final Map<String, ChronoUnit> CHRONOS_UNIT_MAPPING = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
        CHRONOS_UNIT_MAPPING.put(CHRONOS_UNIT_STR_SECONDS, ChronoUnit.SECONDS);
        CHRONOS_UNIT_MAPPING.put(CHRONOS_UNIT_STR_MINUTES, ChronoUnit.MINUTES);
        CHRONOS_UNIT_MAPPING.put(CHRONOS_UNIT_STR_HOURS, ChronoUnit.HOURS);
        CHRONOS_UNIT_MAPPING.put(CHRONOS_UNIT_STR_DAYS, ChronoUnit.DAYS);
        CHRONOS_UNIT_MAPPING.put(CHRONOS_UNIT_STR_WEEKS, ChronoUnit.WEEKS);
        CHRONOS_UNIT_MAPPING.put(CHRONOS_UNIT_STR_MONTHS, ChronoUnit.MONTHS);
        CHRONOS_UNIT_MAPPING.put(CHRONOS_UNIT_STR_YEARS, ChronoUnit.YEARS);
    }

    /**
     * 根据传入的字符串时间单位获取对应的{@link ChronoUnit}类型
     *
     * @param unit 字符串类型的时间单位
     * @return 此时间单位的ChronUnit类型，找不到时返回null
     */
    public static ChronoUnit toChronoUnit(String unit) {
        return CHRONOS_UNIT_MAPPING.get(unit);
    }

    /**
     * 时间切片方法（重载）
     *
     * @return 时间切片集合
     */
    public static List<Map.Entry<LocalDateTime, LocalDateTime>> timeSlicing(LocalDateTime start, LocalDateTime end,
                                                                            long delta, TemporalUnit unit) {
        return timeSlicing(start, end, delta, unit, true);
    }

    /**
     * 时间切片方法
     * <p>
     * 调用者给定时间范围，此方法则会按照时间单位和时间增量生成若干份连续的时间片段
     *
     * @param start     开始时间
     * @param end       结束时间
     * @param delta     时间增量
     * @param unit      时间单位
     * @param hasFuture 是否生成有未来时间的切片（false：生成的切片时间最终结束段为当前时间，而非传入的值）
     * @return 时间切片集合，存储的都是片段的开始时间和结束时间
     */
    public static List<Map.Entry<LocalDateTime, LocalDateTime>> timeSlicing(LocalDateTime start, LocalDateTime end,
                                                                            long delta, TemporalUnit unit,
                                                                            boolean hasFuture) {
        // 是否生成有未来时间的切片组
        if (!hasFuture) {
            LocalDateTime now = LocalDateTime.now();
            // 判断当前时间是否早于切片的结束时间，若是，则替换结束时间为当前时间
            if (now.isBefore(end)) {
                end = now;
            }
        }

        LocalDateTime startSegment = start;
        LocalDateTime endSegment;
        List<Map.Entry<LocalDateTime, LocalDateTime>> timeSlicing = new ArrayList<>();
        while (startSegment.isBefore(end)) {
            endSegment = startSegment.plus(delta, unit);
            if (endSegment.isAfter(end)) {
                endSegment = end;
            }
            timeSlicing.add(new AbstractMap.SimpleImmutableEntry<>(startSegment, endSegment));
            startSegment = endSegment;
        }
        return timeSlicing;
    }
}
