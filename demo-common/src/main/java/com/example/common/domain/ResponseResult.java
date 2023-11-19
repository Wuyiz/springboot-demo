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
        this(data, RespResultEnum.SUCCESS);
    }

    private ResponseResult(RespResultEnum resultEnum) {
        this(null, resultEnum);
    }

    private ResponseResult(T data, RespResultEnum resultEnum) {
        this(data, resultEnum.code, resultEnum.message);
    }

    private ResponseResult(RespResultEnum resultEnum, String message) {
        this(null, resultEnum.code, message);
    }

    private ResponseResult(T data, RespResultEnum resultEnum, String message) {
        this(data, resultEnum.code, message);
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
        return new ResponseResult<>(data, RespResultEnum.ERROR);
    }

    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<>(RespResultEnum.ERROR, message);
    }

    public static <T> ResponseResult<T> error(T data, String message) {
        return new ResponseResult<>(data, RespResultEnum.ERROR, message);
    }

    public static <T> ResponseResult<T> error(RespResultEnum resultEnum) {
        if (Objects.isNull(resultEnum)) {
            return error();
        }
        return new ResponseResult<>(resultEnum);
    }

    public static <T> ResponseResult<T> error(T data, RespResultEnum resultEnum) {
        if (Objects.isNull(resultEnum)) {
            return error(data);
        }
        return new ResponseResult<>(data, resultEnum);
    }

    public static <T> ResponseResult<T> error(RespResultEnum resultEnum, String message) {
        if (Objects.isNull(resultEnum)) {
            return error(message);
        }
        return new ResponseResult<>(resultEnum, message);
    }

    public static <T> ResponseResult<T> error(T data, RespResultEnum resultEnum, String message) {
        if (Objects.isNull(resultEnum)) {
            return error(data, message);
        }
        return new ResponseResult<>(data, resultEnum, message);
    }
}