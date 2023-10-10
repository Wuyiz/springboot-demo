package com.example.redis.listener;


import com.example.redis.message.DemoMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class DemoListener implements StreamListener<String, ObjectRecord<String, DemoMessage>> {
    @Override
    public void onMessage(ObjectRecord<String, DemoMessage> message) {
        String stream = message.getStream();
        RecordId recordId = message.getId();
        DemoMessage eventMessage = message.getValue();
        log.info("[自动] 接收到一个消息 stream:[{}],recordId:[{}],value:[{}]", stream, recordId, eventMessage);
        if (Objects.equals("evaluation_key", eventMessage.getProcessDefKey())) {
            // do something
        }
    }

}
