package com.example.common.result;

import java.io.Serializable;
import java.util.Objects;

/**
 * 请求响应结果集封装类
 *
 * @author wuyizhang
 * @Date 2023-07-03
 */
public class ResponseResult<T> implements Serializable {
    private final Boolean status;
    private final Integer code;
    private final String message;
    private final T data;

    private static final long serialVersionUID = 1L;

    private ResponseResult(RespCodeEnum code) {
        this(code, null);
    }

    private ResponseResult(T data) {
        this(RespCodeEnum.SUCCESS, data);
    }

    private ResponseResult(RespCodeEnum code, T data) {
        this(code.status, code.code, code.message, data);
    }

    private ResponseResult(RespCodeEnum code, String message, T data) {
        this(code.status, code.code, message, data);
    }

    private ResponseResult(Boolean status, Integer code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(RespCodeEnum.SUCCESS);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(data);
    }

    public static <T> ResponseResult<T> error() {
        return new ResponseResult<>(RespCodeEnum.ERROR);
    }

    public static <T> ResponseResult<T> error(T data) {
        return new ResponseResult<>(RespCodeEnum.ERROR, data);
    }

    public static <T> ResponseResult<T> error(RespCodeEnum code) {
        return new ResponseResult<>(RespCodeEnum.ERROR);
    }

    public static <T> ResponseResult<T> error(RespCodeEnum code, T data) {
        if (Objects.isNull(code)) {
            return error(data);
        }
        return new ResponseResult<>(code, data);
    }

    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<>(RespCodeEnum.ERROR, message, null);
    }

    public static <T> ResponseResult<T> error(String message, T data) {
        return new ResponseResult<>(RespCodeEnum.ERROR, message, data);
    }

    public static <T> ResponseResult<T> error(RespCodeEnum code, String message, T data) {
        if (Objects.isNull(code)) {
            return error(message, data);
        }
        return new ResponseResult<>(code, message, data);
    }

    public static <T> ResponseResult<T> error(Integer code, String message, T data) {
        return new ResponseResult<>(Boolean.FALSE, code, message, data);
    }


    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public String toString() {
        return "Result( code=" + this.getCode() + ", message=" + this.getMessage() + ", status=" + this.getStatus() + ", data=" + this.getData() + ")";
    }
}