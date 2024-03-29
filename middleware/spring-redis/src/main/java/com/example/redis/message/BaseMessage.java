package com.example.redis.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseMessage implements Serializable {
    /**
     * 流程定义的key（模型key）
     */
    private String processDefKey;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    private String businessKey;
}
