package com.example.common.domain;

import com.example.common.enums.RespResultEnum;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

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

    private final int code;
    private final T data;
    private final String message;

    private ResponseResult(int code, T data, String message) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseResult<T> success() {
        return success(null);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(RespResultEnum.SUCCESS.getCode(), data, RespResultEnum.SUCCESS.getMessage());
    }

    public static <T> ResponseResult<T> error() {
        return error(RespResultEnum.ERROR);
    }

    public static <T> ResponseResult<T> error(T data) {
        return error(RespResultEnum.ERROR, data);
    }

    public static <T> ResponseResult<T> error(String message) {
        return error(RespResultEnum.ERROR, message);
    }

    public static <T> ResponseResult<T> error(T data, String message) {
        return error(RespResultEnum.ERROR, data, message);
    }

    public static <T> ResponseResult<T> error(RespResultEnum resultEnum) {
        return error(resultEnum, (T) null);
    }

    public static <T> ResponseResult<T> error(RespResultEnum resultEnum, T data) {
        if (Objects.isNull(resultEnum)) {
            resultEnum = RespResultEnum.ERROR;
        }
        return error(resultEnum, data, resultEnum.getMessage());
    }

    public static <T> ResponseResult<T> error(RespResultEnum resultEnum, String message) {
        return error(resultEnum, null, message);
    }

    public static <T> ResponseResult<T> error(RespResultEnum resultEnum, T data, String message) {
        if (Objects.isNull(resultEnum)) {
            resultEnum = RespResultEnum.ERROR;
        }
        return new ResponseResult<>(resultEnum.getCode(), data, message);
    }
}