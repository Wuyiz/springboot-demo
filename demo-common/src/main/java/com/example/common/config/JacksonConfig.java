package com.example.common.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson全局序列化配置
 *
 * @author suhai
 * @since 2023-05-05
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Jackson2ObjectMapperBuilder.class)
public class JacksonConfig {
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            configureJavaTimeSerializer(builder);
            // todo 反序列化配置不生效
            // 已发现问题2：在feign调用时，如果远程接口响应参数的localDateTime格式与全局反序列化格式不一致时，会报反序列化异常
            // 建议：在feign调用时,手动加@JsonFormat传入的格式，避免反序列化失败
            // 注意：@JsonFormat序列化和反序列化优先级高于自定义的全局配置，配置了此注解的localDateTime将会执行注解参数里的值进行序列化和反序列化
            // configureJavaTimeDeserializer(builder);
            configureNumberTypeSerializer(builder);
        };
    }

    private void configureJavaTimeSerializer(Jackson2ObjectMapperBuilder builder) {
        builder.serializerByType(LocalDate.class, new LocalDateSerializer(dateFormatter));
        builder.serializerByType(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
    }

    private void configureJavaTimeDeserializer(Jackson2ObjectMapperBuilder builder) {
        builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        builder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
    }

    /**
     * 超大整数类型序列化为String，解决后端响应数据给前端出现精度丢失问题
     * <p>
     * 原因：前端处理整数数据时，会将其转换成Number类型进行操作，而Number的精度范围在 -2^53 到 2^53 之间（不含两个端点），
     * 即 -9007199254740991 ~ 9007199254740991，超出范围则有可能产生精度丢失。
     */
    private void configureNumberTypeSerializer(Jackson2ObjectMapperBuilder builder) {
        builder.serializerByType(Long.class, ToStringSerializer.instance);
        builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
        builder.serializerByType(BigInteger.class, ToStringSerializer.instance);
    }
}
