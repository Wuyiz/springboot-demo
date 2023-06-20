package com.example.kafka.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class KafkaProducer {
    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * kafka：单个数据推送
     *
     * @param topic 主题名称
     * @param data  数据对象（one）
     */
    public void sendData(String topic, Object data) {
        TimeInterval timer = DateUtil.timer();
        log.debug("kafka生产消息推送：{}", topic);
        // 序列化数据对象，保留null字段，格式化Date对象日期格式
        String dataStr = JSON.toJSONString(data, DatePattern.NORM_DATETIME_PATTERN,
                JSONWriter.Feature.WriteMapNullValue);
        sendData2Kafka(topic, dataStr);
        log.debug("kafka消息推送花费时间： {} ms", timer.intervalRestart());
    }

    /**
     * kafka：集合数据推送
     *
     * @param topic    主题名称
     * @param dataList 数据集合（collection）
     */
    public void sendDataList(String topic, List<?> dataList) {
        TimeInterval timer = DateUtil.timer();
        log.debug("kafka生产批量消息推送：{}", topic);
        for (Object data : dataList) {
            // 序列化数据对象，保留null字段，格式化Date对象日期格式
            String dataStr = JSON.toJSONString(data, DatePattern.NORM_DATETIME_PATTERN,
                    JSONWriter.Feature.WriteMapNullValue);
            sendData2Kafka(topic, dataStr);
        }
        log.debug("kafka批量消息推送花费时间： {} ms，总共推送数据{}条", timer.intervalRestart(), dataList.size());
    }

    private void sendData2Kafka(String topic, Object data) {
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, data);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("kafka sendMessage error, ex = {}, topic = {}, data = {}", ex, topic, data);
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("kafka sendMessage success topic = {}, data = {}", topic, data);
            }
        });
    }

}
