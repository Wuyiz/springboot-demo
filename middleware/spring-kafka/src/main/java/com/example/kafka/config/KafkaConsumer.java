package com.example.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(topics = {"my-topic", "my-test"})
    public void listener1(ConsumerRecord<Object, Object> record, Acknowledgment ack) {
        consumer(record, ack);
    }

    private void consumer(ConsumerRecord<Object, Object> record, Acknowledgment ack) {
        String topic = record.topic();
        String message = (String) record.value();
        long offset = record.offset();
        try {
            log.info("[topic：{}] 读取的消息：{}\t当前偏移量：{}", topic, message, offset);
            // 采用手动提交
            ack.acknowledge();
        } catch (Exception e) {
            log.error("[topic：{}] Kafka消息消费异常，错误原因为：{}", topic, e.getMessage());
        } finally {
            // 最终手动提交，防止异常捕获漏提交，重复消费
            ack.acknowledge();
        }
    }
}