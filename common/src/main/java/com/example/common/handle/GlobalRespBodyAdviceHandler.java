package com.example.common.handle;

import com.example.common.contant.BaseConstants;
import com.example.common.result.ResponseResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应拦截类：统一响应结果类型
 *
 * <p>
 * 注意：ControllerAdvice需要配置basePackages限定响应的拦截范围（避免swagger等其他依赖的接口响应数据也被拦截，导致接口文档加载异常）
 *
 * @author Suhai
 * @Date 2023-10-13
 */
@RestControllerAdvice(basePackages = {BaseConstants.PROJECT_BASE_PATH})
public class GlobalRespBodyAdviceHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 当接口返回的类型为string时，响应处理器会根据已添加的转换器解析出支持string的MimeType集合，即[text/plain, application/json]，
     * 这里text/plain的排序靠前，加上StringHttpMessageConverter在转换器中排序也靠前，所以会优先匹配。
     * 但是转换器是在beforeBodyWrite之前确定好的，而我们又在beforeBodyWrite里对响应数据做了封装处理，
     * 导致后续的转换过程中会出现StringHttpMessageConverter无法转换类的异常
     * <p>
     * 解决办法：在springMvc中移除StringHttpMessageConverter转换器，或者将MappingJackson2HttpMessageConverter设置到最前面
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        if (body instanceof ResponseResult) {
            return body;
        }

        return ResponseResult.success(body);
    }
}
