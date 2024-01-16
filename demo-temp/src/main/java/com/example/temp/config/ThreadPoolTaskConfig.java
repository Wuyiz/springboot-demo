package com.example.temp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池配置
 */
@Configuration
public class ThreadPoolTaskConfig {
    /** 核心线程数（默认线程数） */
    private static final int CORE_POOL_SIZE = 20;
    /** 最大线程数 */
    private static final int MAX_POOL_SIZE = 100;
    /** 允许线程空闲时间（单位：默认为秒） */
    private static final int KEEP_ALIVE_TIME = 10;
    /** 缓冲队列大小 */
    private static final int QUEUE_CAPACITY = 200;
    /** 线程池名前缀 */
    private static final String THREAD_NAME_PREFIX = "temp-async-";

    /**
     * 拒绝处理策略
     * CallerRunsPolicy()：交由调用方线程运行，比如 main 线程。
     * AbortPolicy()：直接抛出异常。
     * DiscardPolicy()：直接丢弃。
     * DiscardOldestPolicy()：丢弃队列中最老的任务。
     */
    @Bean("taskExecutor") // bean的名称，默认为首字母小写的方法名
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(KEEP_ALIVE_TIME);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);

        // 线程池对拒绝任务的处理策略
        // CallerRunsPolicy：由调用线程（提交任务的线程）处理该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程初始化，如果线程池交由spring管理时，springboot会自动调用方法进行初始化，用户无需额外操作
        // executor.initialize();
        return executor;
    }
}