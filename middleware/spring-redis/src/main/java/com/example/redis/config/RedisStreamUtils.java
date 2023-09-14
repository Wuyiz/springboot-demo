package com.example.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;

/**
 * Redis Stream 工具类
 */
@Slf4j
public class RedisStreamUtils {
    // redis stream轮询监听队列线程池（一条队列在监听时，始终会占用线程池内的同一个线程，线程池大小取决于有多少监听器）
    private static final ThreadPoolTaskScheduler STREAM_TASK_SCHEDULER = initRedisStreamTaskScheduler();

    public static <NV> StreamMessageListenerContainer<String, ObjectRecord<String, NV>> registerListenerContainer(
            RedisConnectionFactory factory, String streamKey, String groupName, String consumerName,
            Class<NV> targetType, StreamListener<String, ObjectRecord<String, NV>> listener) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, NV>> options
                = createContainerOptions(targetType);
        StreamMessageListenerContainer<String, ObjectRecord<String, NV>> listenerContainer
                = StreamMessageListenerContainer.create(factory, options);
        StreamMessageListenerContainer.StreamReadRequest<String> readRequest = createReadRequest(streamKey, groupName, consumerName);
        listenerContainer.register(readRequest, listener);
        log.info("Redis Stream：消息队列{}监听容器注册，消费组{}，消费者{}", streamKey, groupName, consumerName);
        return listenerContainer;
    }

    private static <NV> StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, NV>>
    createContainerOptions(Class<NV> targetType) {
        return StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .batchSize(5)
                .executor(STREAM_TASK_SCHEDULER)
                .pollTimeout(Duration.ofSeconds(2))
                .targetType(targetType)
                .build();
    }

    private static StreamMessageListenerContainer.StreamReadRequest<String> createReadRequest(
            String streamKey, String groupName, String consumerName) {
        return StreamMessageListenerContainer.StreamReadRequest
                .builder(StreamOffset.create(streamKey, ReadOffset.lastConsumed()))
                .consumer(Consumer.from(groupName, consumerName))
                .autoAcknowledge(true)
                .cancelOnError(t -> false)
                .build();
    }

    private static ThreadPoolTaskScheduler initRedisStreamTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(20);
        taskScheduler.setBeanName("Redis-Stream-Watch-Scheduler");
        taskScheduler.initialize();
        return taskScheduler;
    }
}