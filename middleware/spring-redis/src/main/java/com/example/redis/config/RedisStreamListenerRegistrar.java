package com.example.redis.config;

import com.example.redis.listener.DemoListener;
import com.example.redis.message.DemoMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * redis stream 监听注册器
 */
@Slf4j
@Component
public class RedisStreamListenerRegistrar {
    public static final String GROUP = "demo-group";
    public static final String CONSUMER = "demo-consumer";
    public static final String STREAM = "demo:testStream";


    @Resource(name = RedisConfig.BEAN_NAME_REDIS_STREAM_TEMPLATE)
    public RedisTemplate<String, Object> redisStreamTemplate;

    @Bean(initMethod = "start")
    public StreamMessageListenerContainer<String, ObjectRecord<String, DemoMessage>> processEndListener(
            RedisConnectionFactory factory, DemoListener listener) {
        RedisStreamUtils.initializeStreamAndGroup(STREAM, GROUP, redisStreamTemplate);
        return RedisStreamUtils.registerListenerContainer(factory, STREAM, GROUP, CONSUMER, DemoMessage.class, listener);
    }


}
