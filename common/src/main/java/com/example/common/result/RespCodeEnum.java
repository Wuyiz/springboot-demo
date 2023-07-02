package com.example.common.result;

/**
 * 响应码枚举类
 *
 * @author wuyizhang
 * @Date 2023-07-03
 */
public enum RespCodeEnum {
    SUCCESS(true, RespCodeConstants.SUCCESS_CODE, "成功"),

    ERROR(false, RespCodeConstants.ERROR_CODE, "失败"),

    ;

    final boolean status;
    final int code;
    final String message;

    RespCodeEnum(boolean status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
