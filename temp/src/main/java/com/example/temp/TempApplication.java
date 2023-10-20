package com.example.temp;

import com.example.common.contant.BaseConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = BaseConstants.PROJECT_BASE_PATH, exclude = {DataSourceAutoConfiguration.class})
public class TempApplication {

    public static void main(String[] args) {
        SpringApplication.run(TempApplication.class, args);
    }

}
