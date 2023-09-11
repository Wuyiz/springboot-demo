package com.example.redis.config;

import com.example.redis.listener.ProcessMessageListener;
import com.example.redis.message.ProcessEndMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * redis stream 监听注册器
 */
@Slf4j
@Component
public class RedisStreamListenerRegistrar {
    public static final String GROUP = "jz-event-group";
    public static final String CONSUMER = "jz-event-consumer";
    public static final String STREAM_WORKFLOW_TASK = "workflow:event:task";
    public static final String STREAM_WORKFLOW_PROCESS = "workflow:event:process";


    @Resource
    public RedisTemplate<String, Object> redisStreamTemplate;
    @Resource
    private ProcessMessageListener processMessageListener;

    @Bean(initMethod = "start")
    public StreamMessageListenerContainer<String, ObjectRecord<String, ProcessEndMessage>> processEndListener(
            RedisConnectionFactory factory) {
        initializeStreamAndGroup(STREAM_WORKFLOW_PROCESS, GROUP);
        return RedisStreamUtils.registerListenerContainer(factory, STREAM_WORKFLOW_PROCESS, GROUP, CONSUMER,
                ProcessEndMessage.class, processMessageListener);
    }

    private void initializeStreamAndGroup(String streamKey, String groupName) {
        boolean hasStream = Boolean.TRUE.equals(redisStreamTemplate.hasKey(streamKey));
        boolean hasGroup = false;
        if (hasStream) {
            StreamInfo.XInfoGroups groups = redisStreamTemplate.opsForStream().groups(streamKey);
            hasGroup = groups.stream().anyMatch(m -> Objects.equals(m.groupName(), groupName));
        }
        boolean initialized = hasStream && hasGroup;
        // 初始化stream和group
        if (!initialized) {
            log.info("Redis Stream：初始化消息队列{}，消费组{}", streamKey, groupName);
            StreamOffset<String> streamOffset = StreamOffset.fromStart(streamKey);
            redisStreamTemplate.opsForStream().createGroup(streamOffset.getKey(), streamOffset.getOffset(), groupName);
        }
    }


}
