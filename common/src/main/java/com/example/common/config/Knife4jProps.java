package com.example.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "knife4j-custom")
public class Knife4jProps {
    private Boolean enable = true;

    private String title;

    private String description;

    private String version;

    private String basePackage;
}
