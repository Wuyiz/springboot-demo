package com.example.common.result;

/**
 * 响应码枚举类
 *
 * @author suhai
 * @since 2023-07-03
 */
public enum RespCodeEnum {
    SUCCESS(true, 200, "成功"),
    ERROR(false, 500, "失败");

    final boolean status;
    final int code;
    final String message;

    RespCodeEnum(boolean status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
