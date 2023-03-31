package com.example.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Optional;

@Component
public class Knife4jRunnerLog implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(Knife4jRunnerLog.class);

    private final ApplicationContext applicationContext;

    public Knife4jRunnerLog(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Environment env = applicationContext.getEnvironment();
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
