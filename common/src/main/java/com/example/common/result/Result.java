package com.example.common.result;

import java.io.Serializable;
import java.util.Objects;

public class Result<T> implements Serializable {
    private final Boolean status;
    private final Integer code;
    private final String message;
    private final T data;

    private static final long serialVersionUID = 1L;

    private Result(ResultCode code) {
        this(code, null);
    }

    private Result(T data) {
        this(ResultCode.SUCCESS, data);
    }

    private Result(ResultCode code, T data) {
        this(code.status, code.code, code.message, data);
    }

    private Result(ResultCode code, String message, T data) {
        this(code.status, code.code, message, data);
    }

    private Result(Boolean status, Integer code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> error() {
        return new Result<>(ResultCode.ERROR);
    }

    public static <T> Result<T> error(T data) {
        return new Result<>(ResultCode.ERROR, data);
    }

    public static <T> Result<T> error(ResultCode code) {
        return new Result<>(ResultCode.ERROR);
    }

    public static <T> Result<T> error(ResultCode code, T data) {
        if (Objects.isNull(code)) {
            return error(data);
        }
        return new Result<>(code, data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.ERROR, message, null);
    }

    public static <T> Result<T> error(String message, T data) {
        return new Result<>(ResultCode.ERROR, message, data);
    }

    public static <T> Result<T> error(ResultCode code, String message, T data) {
        if (Objects.isNull(code)) {
            return error(message, data);
        }
        return new Result<>(code, message, data);
    }

    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(Boolean.FALSE, code, message, data);
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