package com.example.redis.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Flowable事件消息：任务创建（TASK_CREATED）
 *
 * @author wuyizhang
 * @Date 2023-08-31
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TaskCreateMessage extends BaseMessage {
    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务节点key
     */
    private String taskDefKey;

    /**
     * 任务创建时间
     */
    private LocalDateTime taskCreateTime;
}
