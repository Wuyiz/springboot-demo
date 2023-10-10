package com.example.redis.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DemoMessage extends BaseMessage {
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
