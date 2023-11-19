package com.example.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * knife4j 接口文档配置类
 *
 * @author suhai
 * @since 2023-07-03
 */
@Configuration
public class Knife4jConfig {
    private final Knife4jConfigProperties knife4jConfigProperties;

    public Knife4jConfig(Knife4jConfigProperties knife4jConfigProperties) {
        this.knife4jConfigProperties = knife4jConfigProperties;
    }

    @Bean
    public Docket docketBean() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(knife4jConfigProperties.getEnable())
                .apiInfo(apiInfo())
                // 接口文档显示响应参数时，将ResponseEntity<MyModel>替换为MyModel
                // .genericModelSubstitutes(ResponseResult.class)
                // 接口文档显示LocalTime格式时不是常规格式，这里做统一替换处理
                .directModelSubstitute(LocalDate.class, Date.class)
                .directModelSubstitute(LocalTime.class, Date.class)
                .directModelSubstitute(LocalDateTime.class, Date.class)
                // 分组名称
                // .groupName("api分组名称")
                .select()
                // 指定Controller扫描包路径
                // .apis(RequestHandlerSelectors.any())
                .apis(RequestHandlerSelectors.basePackage(knife4jConfigProperties.getBasePackage()))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(knife4jConfigProperties.getTitle())
                .version(knife4jConfigProperties.getVersion())
                .description(knife4jConfigProperties.getDescription())
                .termsOfServiceUrl("https://doc.xiaominfo.com/docs/quick-start")
                .contact(new Contact("Wuyiz", "https://cnblogs.com/suhai", "wuyiz@foxmail.com"))
                .build();
    }

    /**
     * WebServer准备就绪时发布事件
     * <p>
     * 好处是可以使用WebServerInitializedEvent获取正在运行的服务器的本地端口
     */
    @Component
    static class Knife4jStartupLog implements ApplicationListener<WebServerInitializedEvent> {
        private static final Logger logger = LoggerFactory.getLogger(Knife4jStartupLog.class);

        @Resource
        private Environment env;
        @Resource
        private WebApplicationContext webApplicationConnect;

        @Override
        public void onApplicationEvent(@NonNull WebServerInitializedEvent webServerInitializedEvent) {
            String hostAddress = null;
            try {
                hostAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                logger.error("获取本地ip地址失败", e);
            }
            int serverPort = webServerInitializedEvent.getWebServer().getPort();
            String applicationName = Optional.ofNullable(env.getProperty("spring.application.name")).orElse("");
            String contextPath = Objects.requireNonNull(webApplicationConnect.getServletContext()).getContextPath();
            logger.info("\n-----------------------------------------------------------------\n\t" +
                            "Application '{}' is running! Access URLs:\n\t" +
                            "Local: \t\thttp://localhost:{}{}\n\t" +
                            "External: \thttp://{}:{}{}\n\t" +
                            "Doc: \t\thttp://{}:{}{}/doc.html\n" +
                            "-----------------------------------------------------------------",
                    applicationName,
                    serverPort, contextPath,
                    hostAddress, serverPort, contextPath,
                    hostAddress, serverPort, contextPath);
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

            logger.info("\n-----------------------------------------------------------------\n" +
                            "Application '{}' is running! Access URLs:\n\t" +
                            "Local: \t\thttp://localhost:{}{}\n\t" +
                            "External: \thttp://{}:{}{}\n\t" +
                            "Doc: \t\thttp://{}:{}{}/doc.html\n" +
                            "-----------------------------------------------------------------",
                    applicationName,
                    serverPort, contextPath,
                    hostAddress, serverPort, contextPath,
                    hostAddress, serverPort, contextPath);
        }
    }
}
