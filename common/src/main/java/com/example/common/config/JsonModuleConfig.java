package com.example.common.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigInteger;

/**
 * Json全局配置
 *
 * @author wuyiz
 * @Date 2023-07-03
 */
// @Configuration
public class JsonModuleConfig extends SimpleModule {

    public JsonModuleConfig() {
        // super(JsonModuleConfig.class.getName());

        // 前端接收长整型和BigInteger类型时会产生精度缺失问题，此处全局转换为字符串类型
        this.addSerializer(Long.class, ToStringSerializer.instance);
        this.addSerializer(Long.TYPE, ToStringSerializer.instance);
        this.addSerializer(BigInteger.class, ToStringSerializer.instance);
    }
}