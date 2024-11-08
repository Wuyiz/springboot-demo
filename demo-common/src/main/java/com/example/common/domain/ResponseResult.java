package com.example.common.domain;

import com.example.common.enums.RespResultEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * 统一封装请求响应结果集
 * <hr>
 * <p>
 * <b>Jackson：</b>反序列化时，必须存在无参构造（任何访问修饰符均可），否则反序列化失败；目标对象即使没有任何setter方法，也仍然可以给对象内的属性进行赋值
 * <br>
 * <b>Fastjson：</b>反序列化时，无参构造>全参构造>有参构造<br>
 * 1.只显式声明了一个无参构造器（任何访问修饰符均可），或者没有显式声明任何构造器时，则反序列化时只会对存在setter方法的属性进行赋值；<br>
 * 2.只显式声明了一个有参构造器（public）时，则只会使用这个构造器构造对象，且反序列化时被赋值的属性和构造器参数相关（不会调用任何setter方法赋值）；<br>
 * 3.显示声明了多个有参构造器时，按照无参构造>全参构造>有参构造的优先级构造对象，且反序列化时被赋值的属性和构造器参数相关（不会调用任何setter方法赋值）；
 * <p>
 * <b>结论：想要同时支持Jackson和Fastjson的反序列化，就必须包含setter方法和一个无参构造器</b>
 *
 * @author suhai
 * @since 2023-07-03
 */
@Getter
@Setter
@ToString
public final class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 响应数据
     */
    private T data;
    /**
     * 响应状态码
     */
    private int code;
    /**
     * 响应消息
     */
    private String message;

    private ResponseResult() {
    }

    public static <T> ResponseResult<T> success() {
        return success(null);
    }

    public static <T> ResponseResult<T> success(T data) {
        return of(data, RespResultEnum.SUCCESS.getCode(), RespResultEnum.SUCCESS.getMessage());
    }

    public static <T> ResponseResult<T> error() {
        return error(RespResultEnum.ERROR);
    }

    public static <T> ResponseResult<T> error(@Nonnull RespResultEnum resultEnum) {
        return error(resultEnum, resultEnum.getMessage());
    }

    public static <T> ResponseResult<T> error(String message) {
        return error(RespResultEnum.ERROR, message);
    }

    public static <T> ResponseResult<T> error(@Nonnull RespResultEnum resultEnum, String message) {
        return of(null, resultEnum.getCode(), message);
    }

    private static <T> ResponseResult<T> of(T data, int code, String message) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setData(data);
        responseResult.setCode(code);
        responseResult.setMessage(message);
        return responseResult;
    }
}