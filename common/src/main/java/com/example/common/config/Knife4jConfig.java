package com.example.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

// @EnableKnife4j
@EnableSwagger2WebMvc
@Configuration
public class Knife4jConfig {

    @Bean
    @Order(1)
    public Docket docketBean() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(true)
                .apiInfo(apiInfo())
                // 分组名称
                .groupName("测试分组123")
                .select()
                // 这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Knife4j接口文档——标题")
                .description("Knife4j接口文档——description")
                .termsOfServiceUrl("https://doc.xiaominfo.com/docs/quick-start")
                .contact(new Contact("Wuyiz", "https://cnblogs.com/suhai", "wuyiz@foxmail.com"))
                .version("1.0.0")
                .build();
    }
}
