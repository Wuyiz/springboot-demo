package com.example.redis.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Flowable事件消息：流程结束（HISTORIC_PROCESS_INSTANCE_ENDED）
 *
 * @author wuyizhang
 * @Date 2023-07-27
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProcessEndMessage extends BaseMessage {
    /**
     * 发起时间
     */
    private LocalDateTime processStartTime;

    /**
     * 结束时间
     */
    private LocalDateTime processEndTime;

    /**
     * 流程状态
     */
    private Integer processStatus;
}
