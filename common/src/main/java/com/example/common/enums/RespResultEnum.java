package com.example.common.enums;

/**
 * 响应码枚举类
 *
 * @author suhai
 * @since 2023-07-03
 */
public enum RespResultEnum {
    SUCCESS(200, "success"),
    ERROR(500, "error"),
    ;

    public final int code;
    public final String message;

    RespResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
