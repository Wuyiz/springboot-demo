package com.example.common.enums;

import lombok.Getter;

/**
 * 响应码枚举类
 *
 * @author suhai
 * @since 2023-07-03
 */
@Getter
public enum RespResultEnum {
    SUCCESS(200, "success"),
    ERROR(500, "error");

    private final int code;
    private final String message;

    RespResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
