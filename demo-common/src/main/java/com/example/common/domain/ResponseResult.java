package com.example.common.domain;

import com.example.common.enums.RespResultEnum;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

/**
 * 请求响应结果集封装类
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

    private ResponseResult(T data) {
        this(RespResultEnum.SUCCESS, data);
    }

    private ResponseResult(RespResultEnum resultEnum) {
        this(resultEnum, (T) null);
    }

    private ResponseResult(RespResultEnum resultEnum, T data) {
        this(data, resultEnum.getCode(), resultEnum.getMessage());
    }

    private ResponseResult(RespResultEnum resultEnum, String message) {
        this(null, resultEnum.getCode(), message);
    }

    private ResponseResult(RespResultEnum resultEnum, T data, String message) {
        this(data, resultEnum.getCode(), message);
    }

    private ResponseResult(T data, int code, String message) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(RespResultEnum.SUCCESS);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(data);
    }

    public static <T> ResponseResult<T> error() {
        return new ResponseResult<>(RespResultEnum.ERROR);
    }

    public static <T> ResponseResult<T> error(T data) {
        return new ResponseResult<>(RespResultEnum.ERROR, data);
    }

    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<>(RespResultEnum.ERROR, message);
    }

    public static <T> ResponseResult<T> error(T data, String message) {
        return new ResponseResult<>(RespResultEnum.ERROR, data, message);
    }

    public static <T> ResponseResult<T> error(RespResultEnum resultEnum) {
        if (Objects.isNull(resultEnum)) {
            return error();
        }
        return new ResponseResult<>(resultEnum);
    }

    public static <T> ResponseResult<T> error(RespResultEnum resultEnum, T data) {
        if (Objects.isNull(resultEnum)) {
            return error(data);
        }
        return new ResponseResult<>(resultEnum, data);
    }

    public static <T> ResponseResult<T> error(RespResultEnum resultEnum, String message) {
        if (Objects.isNull(resultEnum)) {
            return error(message);
        }
        return new ResponseResult<>(resultEnum, message);
    }

    public static <T> ResponseResult<T> error(RespResultEnum resultEnum, T data, String message) {
        if (Objects.isNull(resultEnum)) {
            return error(data, message);
        }
        return new ResponseResult<>(resultEnum, data, message);
    }
}