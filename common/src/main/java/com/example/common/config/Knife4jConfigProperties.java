package com.example.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * knife4j 自定义配置属性对象
 *
 * @author wuyiz
 * @Date 2023-07-03
 */
@Data
@Component
@ConfigurationProperties(prefix = "knife4j-custom")
public class Knife4jConfigProperties {
    private Boolean enable = true;

    private String title;

    private String description;

    private String version;

    private String basePackage;
}
