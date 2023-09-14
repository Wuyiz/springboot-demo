package com.example.temp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
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

    @Bean(initMethod = "customInit", destroyMethod = "stop")
    BeanLifeCycleTest test1() {
        return new BeanLifeCycleTest();
    }

    @Slf4j
    public static class BeanLifeCycleTest implements InitializingBean, DisposableBean, Lifecycle {
        public void customInit() {
            log.warn("customInit");
        }

        @PostConstruct
        public void PostConstruct() {
            log.warn("PostConstruct");
        }

        static {
            log.warn("static");
        }

        public BeanLifeCycleTest() {
            super();
            log.warn("Construct");
        }

        @PreDestroy
        public void PreDestroy() {
            log.warn("PreDestroy");
        }

        public void customDestroy() {
            log.warn("customDestroy");
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            log.warn("afterPropertiesSet");
        }

        public void shutdown() {
            log.warn("shutdown");
        }

        public void close() {
            log.warn("close");
        }

        @Override
        public void destroy() throws Exception {
            log.warn("destroy");
        }

        @Override
        public void start() {
            log.warn("start");
        }

        @Override
        public void stop() {
            log.warn("stop");
        }

        @Override
        public boolean isRunning() {
            return false;
        }
    }
}
