package com.example.temp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Async默认使用SimpleAsyncTaskExecutor作为线程工具，然而这个类每次都会新建一个线程执行任务，并非线程池
 * 故此我们需要通过自定义线程池bean或者通过，继承AsyncConfigurerSupport或实现AsyncConfigurer对Async默认线程进行声名
 */
@Slf4j
@EnableAsync
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

    @Bean
    public ThreadPoolTaskExecutor asyncDefaultExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：最重要的参数，即使没有任务需要执行也会一直存活
        // 当线程数小于核心线程数时，即使有线程空闲，也会优先创建线程
        executor.setCorePoolSize(2);

        // 最大线程数：核心线程和阻塞队列同时满时，根据此数值开辟新的线程执行任务
        // 如果最大线程池数量也已满，则线程池会根据拒绝策略处理任务
        executor.setMaxPoolSize(4);

        // 线程空闲时间：当线程空闲时间达到keepAliveTime时，线程会退出，直到线程数量=corePoolSize
        // 如果allowCoreThreadTimeout=true，则会直到线程数量=0
        executor.setKeepAliveSeconds(3);

        // 是否允许核心线程会超时关闭：默认false，如果为true，核心线程池再空闲时达到存活时间也会被关闭
        // executor.setAllowCoreThreadTimeOut(true);

        // 任务队列容量（阻塞队列）：当核心线程数达到最大时，新任务会放在队列中排队等待执行
        executor.setQueueCapacity(2);

        // 当线程数已经达到maxPoolSize，且队列已满，会执行拒绝策略来处理新任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        // 默认情况下，此执行器根本不会等待任务终止，它将立即关闭，中断正在进行的任务并清除剩余的任务队列
        // 如果该 "waitForTasksToCompleteOnShutdown" 设置为 true，它将继续执行所有正在进行的任务以及队列中的所有剩余任务
        // 直到超过"awaitTerminationSeconds"种设置的时间，并与容器的其余部分一同关闭
        // executor.setWaitForTasksToCompleteOnShutdown(true);
        // executor.setAwaitTerminationSeconds(20);

        // 自定义线程池每个线程的名称前缀
        executor.setThreadNamePrefix("async-pool-");

        // 线程初始化，如果线程池交由spring管理时，springboot会自动调用方法进行初始化，用户无需额外操作
        // executor.initialize();
        return executor;

    }

    @Override
    public Executor getAsyncExecutor() {
        return asyncDefaultExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Async Method:{} \t Exception：{}", method.getName(), ex.getMessage());
        };
    }
}
