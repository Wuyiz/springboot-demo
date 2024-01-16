package com.example.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Servlet;
import java.util.List;

/**
 * SpringMVC做自定义配置的几种方式：
 * 1.继承WebMvcConfigurerAdapter（spring5.0开始已废弃）
 * 2.继承WebMvcConfigurationSupport（不推荐使用）
 * 3.实现WebMvcConfigurer（推荐）
 * <p>
 * 区别：
 * 1.WebMvcConfigurationSupport：springboot的自动配置类WebMvcAutoConfiguration上有启用条件，
 * 即@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)，表示如果继承了WebMvcConfigurationSupport的Bean对象存在，
 * WebMvcAutoConfiguration就不会自动配置，用户要手动实现所有的mvc配置
 * 2.WebMvcConfigurer：实现此接口，既可以对SpringMVC的配置做出扩展，也不会影响SpringMVC中的默认配置。
 * 这种配置方式相对来说会更加灵活，其原理是springboot会导入DelegatingWebMvcConfiguration类，这是SpringMVC配置的代理类/委托类，
 * 在该类中有一个方法setConfigurers()，该方法会收集应用中所有的WebMvcConfigurer的实现类，
 * 把所有的配置统一汇总到WebMvcConfigurerComposite中，并进行SpringMVC的配置。
 * <p>
 * 注意：在非SpringBoot的环境中使用时，需要配合@EnableWebMvc来生效，这个注解中导入了DelegatingWebMvcConfiguration类；
 * springboot环境中则不需要使用此注解，原因是springboot实现了自动配置
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 移除StringHttpMessageConverter，解决全局响应拦截中返回类型为string时抛出无法cast的异常
        converters.removeIf(StringHttpMessageConverter.class::isInstance);
    }
}