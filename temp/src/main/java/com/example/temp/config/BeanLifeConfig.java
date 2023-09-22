package com.example.temp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * {@code @Bean} 对象生命周期测试
 * <p>
 * 启动顺序：
 * 1.static静态方法  2.Construct构造方法  3.{@link PostConstruct}
 * 4.{@link  InitializingBean#afterPropertiesSet()}（实现即调用）  5.{@link Bean#initMethod()}
 * <br/>
 * 销毁顺序：
 * 1.{@link PreDestroy}  2.{@link DisposableBean#destroy()}（实现即调用）  3.{@link Bean#destroyMethod()}（指定才调用）
 * <p>
 * Note：销毁Bean对象时，如果没有在{@link Bean#destroyMethod()}指定销毁方法时，
 * 容器会默认执行Bean对象里名为{@code close()或shutdown()}方法（两个方法同时存在，则优先调用{@code close()}，都不存在时，无任何调用），
 * 当然如果你的Bean对象实现了{@link DisposableBean#destroy()}，则在销毁时，只会调用{@link DisposableBean#destroy()}，忽略{@code close()、shutdown()}；
 * 如果在{@link Bean#destroyMethod()}中指定了销毁方法，则一定回调此方法，
 * 如果指定的方法为{@link DisposableBean#destroy()} ，则销毁时只会调用一次{@code destroy()}
 */
@Configuration
public class BeanLifeConfig {

    @Bean(initMethod = "myStart", destroyMethod = "stop")
    public BeanLifeCycleTest testLifeCycle() {
        return new BeanLifeCycleTest();
    }

    @Bean
    public BeanSmartLifeCycleTest testSmartLifeCycle() {
        return new BeanSmartLifeCycleTest();
    }

    @Slf4j
    static class BeanLifeCycleTest implements InitializingBean, DisposableBean, Lifecycle {
        static {
            log.warn("static");
        }

        public BeanLifeCycleTest() {
            log.warn("Construct");
        }


        @PostConstruct
        public void PostConstruct() {
            log.warn("PostConstruct");
        }

        @PreDestroy
        public void PreDestroy() {
            log.warn("PreDestroy");
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            log.warn("afterPropertiesSet");
        }

        @Override
        public void destroy() throws Exception {
            log.warn("destroy");
        }

        public void shutdown() {
            log.warn("shutdown");
        }

        public void close() {
            log.warn("close");
        }

        public void myStart() {
            log.warn("myStart");
        }

        public void myDestroy() {
            log.warn("myDestroy");
        }

        @Override
        public void start() {
            running = true;
            log.warn("start");
        }

        @Override
        public void stop() {
            running = false;
            log.warn("stop");
        }

        private boolean running = false;

        @Override
        public boolean isRunning() {
            log.warn("isRunning {}", running);
            return running;
        }
    }

    @Slf4j
    static class BeanSmartLifeCycleTest implements InitializingBean, DisposableBean, SmartLifecycle {
        static {
            log.warn("static");
        }

        public BeanSmartLifeCycleTest() {
            log.warn("Construct");
        }

        @PostConstruct
        public void PostConstruct() {
            log.warn("PostConstruct");
        }

        @PreDestroy
        public void PreDestroy() {
            log.warn("PreDestroy");
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            log.warn("afterPropertiesSet");
        }

        @Override
        public void destroy() throws Exception {
            log.warn("destroy");
        }

        public void shutdown() {
            log.warn("shutdown");
        }

        public void close() {
            log.warn("close");
        }

        public void myStart() {
            log.warn("myStart");
        }

        public void myDestroy() {
            log.warn("myDestroy");
        }

        @Override
        public boolean isAutoStartup() {
            return true;
        }

        @Override
        public void stop(Runnable callback) {
            log.warn("stop callback");
            stop();
            callback.run();
        }

        @Override
        public void start() {
            running = true;
            log.warn("start");
        }

        @Override
        public void stop() {
            running = false;
            log.warn("stop");
        }

        private volatile boolean running = false;

        @Override
        public boolean isRunning() {
            log.warn("isRunning {}", running);
            return running;
        }
    }
}
