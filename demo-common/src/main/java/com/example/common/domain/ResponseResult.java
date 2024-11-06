package com.example.common.domain;

import com.example.common.enums.RespResultEnum;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * 统一封装请求响应结果集
 *
 * @author suhai
 * @since 2023-07-03
 */
@Getter
@ToString
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final T data;
    private final int code;
    private final String message;

    /**
     * 构造方法
     * <p>
     * 注意：必须是public，否则同平台下的其他服务接收响应结果时无法反序列化
     *
     * @param data    响应数据
     * @param code    响应状态码
     * @param message 响应消息
     */
    public ResponseResult(T data, int code, String message) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseResult<T> success() {
        return success(null);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(data, RespResultEnum.SUCCESS.getCode(), RespResultEnum.SUCCESS.getMessage());
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
        return new ResponseResult<>(null, resultEnum.getCode(), message);
    }
}