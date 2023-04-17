package com.example.common.config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;

@Configuration
public class Knife4jConfig {

    @Bean
    @Order(1)
    public Docket docketBean() {
        return new Docket(DocumentationType.SWAGGER_2).enable(true).apiInfo(apiInfo())
                // 分组名称
                .groupName("测试分组123").select()
                // 这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Knife4j接口文档——标题").description("Knife4j接口文档——description").termsOfServiceUrl("https://doc.xiaominfo.com/docs/quick-start").contact(new Contact("Wuyiz", "https://cnblogs.com/suhai", "wuyiz@foxmail.com")).version("1.0.0").build();
    }


    /**
     * WebServer准备就绪时发布事件
     * <p>
     * 好处是可以使用WebServerInitializedEvent获取正在运行的服务器的本地端口
     */
    @Component
    static class Knife4jListenerLog implements ApplicationListener<WebServerInitializedEvent> {
        private static final Logger logger = LoggerFactory.getLogger(Knife4jListenerLog.class);

        @Resource
        private Environment env;
        @Resource
        private WebApplicationContext webApplicationConnect;

        @Override
        public void onApplicationEvent(@NotNull WebServerInitializedEvent webServerInitializedEvent) {
            String hostAddress = null;
            try {
                hostAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                logger.error("获取本地ip地址失败", e);
            }
            String applicationName = Optional.ofNullable(env.getProperty("spring.application.name")).orElse("");
            int serverPort = webServerInitializedEvent.getWebServer().getPort();
            String contextPath = Objects.requireNonNull(webApplicationConnect.getServletContext()).getContextPath();
            logger.info("\n-----------------------------------------------------------------\n\t" + "Application '{}' is running! Access URLs:\n\t" + "Local: \t\thttp://localhost:{}{}\n\t" + "External: \thttp://{}:{}{}\n\t" + "Doc: \t\thttp://{}:{}{}/doc.html\n" + "-----------------------------------------------------------------", applicationName, serverPort, contextPath, hostAddress, serverPort, contextPath, hostAddress, serverPort, contextPath);
        }
    }

    /**
     * 实现springboot的ApplicationRunner或CommandLineRunner类
     * <p>
     * 在服务完全初始化成功后执行指定的方法，这里通过spring的Environment类获取相关配置
     */
    @Component
    static class Knife4jRunnerLog implements ApplicationRunner {
        private static final Logger logger = LoggerFactory.getLogger(Knife4jRunnerLog.class);

        @Resource
        private Environment env;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            String applicationName = Optional.ofNullable(env.getProperty("spring.application.name")).orElse("");
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String serverPort = Optional.ofNullable(env.getProperty("server.port")).orElse("8080");
            String contextPath = Optional.ofNullable(env.getProperty("server.servlet.context-path")).orElse("");

            logger.info("\n-----------------------------------------------------------------\n" + "Application '{}' is running! Access URLs:\n\t" + "Local: \t\thttp://localhost:{}{}\n\t" + "External: \thttp://{}:{}{}\n\t" + "Doc: \t\thttp://{}:{}{}/doc.html\n" + "-----------------------------------------------------------------", applicationName, serverPort, contextPath, hostAddress, serverPort, contextPath, hostAddress, serverPort, contextPath);
        }
    }
}
