package com.example.common.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigInteger;

// @Configuration
public class JsonModuleConfig extends SimpleModule {

    public JsonModuleConfig() {
        //super(JsonModuleConfig.class.getName());
        this.addSerializer(Long.class, ToStringSerializer.instance);
        this.addSerializer(Long.TYPE, ToStringSerializer.instance);
        this.addSerializer(BigInteger.class, ToStringSerializer.instance);
    }
}