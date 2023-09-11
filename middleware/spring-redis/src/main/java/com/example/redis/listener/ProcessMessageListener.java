package com.example.redis.listener;


import com.example.redis.message.ProcessEndMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 流程事件消息监听器
 *
 * @author wuyizhang
 * @Date 2023-08-02
 */
@Slf4j
@Component
public class ProcessMessageListener implements StreamListener<String, ObjectRecord<String, ProcessEndMessage>> {
    @Resource
    private RedisTemplate<String, Object> redisStreamTemplate;

    @Override
    public void onMessage(ObjectRecord<String, ProcessEndMessage> message) {
        String stream = message.getStream();
        RecordId recordId = message.getId();
        ProcessEndMessage eventMessage = message.getValue();
        log.info("[自动] 接收到一个消息 stream:[{}],recordId:[{}],value:[{}]", stream, recordId, eventMessage);
        if (Objects.equals("evaluation_key", eventMessage.getProcessDefKey())) {
            // do something
            redisStreamTemplate.opsForStream().delete(message);
        }
    }

}
