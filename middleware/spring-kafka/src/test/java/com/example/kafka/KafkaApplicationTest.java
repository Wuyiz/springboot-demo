package com.example.kafka;

import com.alibaba.fastjson2.JSONObject;
import com.example.kafka.config.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.Resource;

@SpringBootTest
class KafkaApplicationTest {
    @Resource
    private KafkaProducer producer;

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void contextLoads() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("测试123", "12322");
        producer.sendData("supconit", jsonObject);
    }

}