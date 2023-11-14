package com.example.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Async默认使用SimpleAsyncTaskExecutor作为线程工具，然而这个类每次都会新建一个线程执行任务，并非使用线程池。
 * 故此，我们需要自定义线程池bean，然后在使用注解时绑定线程池bean名称，eg：@Async(线程池beanName)；
 * 或者通过继承AsyncConfigurerSupport类（也可以实现AsyncConfigurer接口，都一样）对Async默认线程进行自定义
 * <p>
 * (1)、CPU密集型 CPU密集型也叫计算密集型，指的是系统的硬盘、内存性能相对CPU要好很多，此时，系统运作大部分的状况是CPU Loading 100%，CPU要读/写I/O(硬盘/内存)，I/O在很短的时间就可以完成，而CPU还有许多运算要处理，CPU Loading 很高。 在多重程序系统中，大部分时间用来做计算、逻辑判断等CPU动作的程序称之CPU bound。例如一个计算圆周率至小数点一千位以下的程序，在执行的过程当中绝大部分时间用在三角函数和开根号的计算，便是属于CPU bound的程序。 CPU bound的程序一般而言CPU占用率相当高。这可能是因为任务本身不太需要访问I/O设备，也可能是因为程序是多线程实现因此屏蔽掉了等待I/O的时间。
 * <p>
 * (2)、IO密集型 IO密集型指的是系统的CPU性能相对硬盘、内存要好很多，此时，系统运作，大部分的状况是CPU在等I/O (硬盘/内存) 的读/写操作，此时CPU Loading并不高。 I/O bound的程序一般在达到性能极限时，CPU占用率仍然较低。这可能是因为任务本身需要大量I/O操作，而pipeline做得不是很好，没有充分利用处理器能力。
 * <p>
 * (3)、先看下机器的CPU核数，然后在设定具体参数： 自己测一下自己机器的核数 System.out.println(Runtime.getRuntime().availableProcessors()); 即CPU核数 = Runtime.getRuntime().availableProcessors()
 * <p>
 * (4)、分析下线程池处理的程序是CPU密集型还是IO密集型 CPU密集型：corePoolSize = CPU核数 + 1 IO密集型：corePoolSize = CPU核数 * 2
 * <hr/>
 * 版权声明：本文为CSDN博主「time Friend」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：<a href="https://blog.csdn.net/tumu6889/article/details/125257712">线程池详解</a>
 *
 * @author suhai
 * @since 2023-07-03
 */
@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    public static final String ASYNC_THREAD_NAME_PREFIX = "async-worker-";

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
        // DiscardPolicy()：丢弃掉该任务但是不抛出异常，不推荐这种(导致使用者没觉察情况发生)
        // DiscardOldestPolicy()：丢弃队列中等待最久的任务，然后把当前任务加入队列中。
        // AbortPolicy()：丢弃任务并抛出 RejectedExecutionException 异常(线程池默认使用此策略)。
        // CallerRunsPolicy()：队列达到最大任务数量，且创建的线程数已经是最大线程池数量时，新进来的任务既不会被抛弃也不会抛出异常，而是由主线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        // 默认情况下，此执行器根本不会等待任务终止，它将立即关闭，中断正在进行的任务并清除剩余的任务队列
        // 如果该 "waitForTasksToCompleteOnShutdown" 设置为 true，它将继续执行所有正在进行的任务以及队列中的所有剩余任务
        // 直到超过"awaitTerminationSeconds"种设置的时间，并与容器的其余部分一同关闭
        // executor.setWaitForTasksToCompleteOnShutdown(true);
        // executor.setAwaitTerminationSeconds(20);

        // 配置装饰器使其生效
        executor.setTaskDecorator(new ContextTaskDecorator());

        // 自定义线程池每个线程的名称前缀
        executor.setThreadNamePrefix(ASYNC_THREAD_NAME_PREFIX);

        // initialize()方法不需要显式调用，可以直接创建ThreadPoolTaskExecutor实例，因为线程池在创建实例时已经自动初始化了。
        // initialize()方法的具体作用是执行一些初始化操作，例如创建线程池所需的队列等。
        // executor.initialize();
        return executor;

    }

    /**
     * IO密集型线程池配置模板，删除注释等其他无需内容
     */
    private ThreadPoolTaskExecutor ioPoolTemplate() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int core = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(core * 2);
        executor.setMaxPoolSize(core * 4);
        executor.setQueueCapacity(200);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(new ContextTaskDecorator());
        executor.setThreadNamePrefix(ASYNC_THREAD_NAME_PREFIX);
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return asyncDefaultExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    /**
     * TaskDecorator装饰器
     * <p>
     * 解决@Async注解的父子线程之间上下文（例如RequestAttributes等）不能共享的问题
     * <p>
     * 注意：需要在创建线程池时加入时将此装饰器
     */
    static class ContextTaskDecorator implements TaskDecorator {
        @NonNull
        @Override
        public Runnable decorate(@NonNull Runnable runnable) {
            // 拿到主线程的上下文对象
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            return () -> {
                try {
                    // 设置到子线程上下文中
                    // 注意：上下文响应数据后，异步方法中继续使用request是错误的、不安全的做法；
                    // 接口在响应数据后，由于request存在复用的机制，程序会将对其进行清理以便下次请求时使用，这将导致子线程中上下文的数据可能为null
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    runnable.run();
                } finally {
                    // 使用完成之后清除子线程中的上下文，避免内存泄露
                    RequestContextHolder.resetRequestAttributes();
                }
            };
        }
    }
}
