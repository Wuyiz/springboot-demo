package com.example.wsmq;

import com.example.common.contant.BaseConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = BaseConstants.PROJECT_BASE_PATH, exclude = {DataSourceAutoConfiguration.class})
public class WebSocketRabbitMqApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketRabbitMqApplication.class, args);
    }
}