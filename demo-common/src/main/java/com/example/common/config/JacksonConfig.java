package com.example.common.config;

import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Jackson全局序列化配置
 * <p>
 * {@link com.fasterxml.jackson.annotation.JsonFormat}在序列化和反序列化上优先级高于自定义的全局配置，
 * 配置了此注解的时间类将会优先执行注解参数里的格式化值进行序列化和反序列化
 *
 * @author suhai
 * @since 2023-05-05
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Jackson2ObjectMapperBuilder.class)
public class JacksonConfig {
    private static final String LOCAL_DATE_PATTERN = "yyyy-MM-dd";
    private static final String LOCAL_TIME_PATTERN = "HH:mm:ss";
    private static final String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String JDK_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    private final SimpleDateFormat jdkDateFormat = new SimpleDateFormat(JDK_DATE_PATTERN);
    private final DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN);
    private final DateTimeFormatter localTimeFormat = DateTimeFormatter.ofPattern(LOCAL_TIME_PATTERN);
    private final DateTimeFormatter localDateTimeFormat = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_PATTERN);

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            configureJavaTimeSerializer(builder);
            configureJavaTimeDeserializer(builder);
            configureNumberTypeSerializer(builder);
        };
    }

    private void configureJavaTimeSerializer(Jackson2ObjectMapperBuilder builder) {
        builder.serializerByType(Date.class, new DateSerializer(false, jdkDateFormat));
        builder.serializerByType(LocalDate.class, new LocalDateSerializer(localDateFormat));
        builder.serializerByType(LocalTime.class, new LocalTimeSerializer(localTimeFormat));
        builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(localDateTimeFormat));
    }

    private void configureJavaTimeDeserializer(Jackson2ObjectMapperBuilder builder) {
        // todo 反序列化配置不够全面
        // builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        // builder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(localDateTimeFormat));
    }

    /**
     * 长整数类型序列化为字符串形式
     * <p>
     * 原因：前端处理长整型数据时，会将其转换成Number类型进行操作，而Number的精度范围在 -2^53 到 2^53 之间（不含两个端点），
     * 即 -9007199254740991 ~ 9007199254740991，一般为16位数字长度，如果超出范围则有可能产生精度丢失的问题。
     * <p>
     * 案例：后端使用雪花算法生成数据ID，由于生成的ID是长整型，且数字位数已然超过前端JS能够处理的精度，所以会出现精度丢失的问题，导致前端取到的ID数据错误。
     */
    private void configureNumberTypeSerializer(Jackson2ObjectMapperBuilder builder) {
        builder.serializerByType(Long.class, ToStringSerializer.instance);
        builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
        builder.serializerByType(BigInteger.class, ToStringSerializer.instance);
    }
}
