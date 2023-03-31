package com.example.wsmq.rabbitMq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.example.wsmq.controller.RabbitController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RabbitConsumer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    private final MessageConverter converter = new SimpleMessageConverter();

    // @RabbitListener(queues = "CS.Label.Realtime.593d4158-f07a-428b-b674-15687fc334cb.b785410ead7241108fd99591f9cbe076")
    public void listener1(Message message) {
        log.debug("队列{}监听消费：{}", "CS.Label.Realtime.柳湖社区铁塔", message);
    }

    // @RabbitListener(queues = "CS.Label.Realtime.e72b0563-33d7-47d7-ac67-b3e699369517.0fee8ced65834817985375d5a79e8f89")
    public void listener2(Message message) {
        log.debug("队列{}监听消费：{}", "CS.Label.Realtime.民防大厦", message);
    }

    // @RabbitListener(queues = "CS.Label.Realtime.e72b0563-33d7-47d7-ac67-b3e699369517.605aa32b423c4258b8c8b630d2350547")
    public void listener3(Message message) {
        log.debug("队列{}监听消费：{}", "CS.Label.Realtime.怡景苑", message);
    }

    // @RabbitListener(queues = "CS.GIS.RealTime.c0e2c560-38f6-4091-856c-125f2371abea")
    public void listener4(Message message) {
        byte[] body = message.getBody();
        String bodyStr = new String(body);
        log.debug("队列{}监听消费：{}", "CS.GIS", message);
        log.debug("队列{}监听转换后：{}", "CS.GIS", bodyStr);
    }

    // @RabbitListener(queues = "label.service.test.queue")
    public void listener5(JSONObject jsonObject, Message message) {
        log.debug("队列{}监听消费：{}", message.getMessageProperties().getConsumerQueue(), message);
        if (jsonObject.getString("code").equals(RabbitController.ACTION_TYPE_SUB)) {
            String data = jsonObject.getString("data");
            rabbitTemplate.convertAndSend("Meta.LabelService.RealTime.LabelData",
                    "LabelData.RealTime." + data + ".PTTYPE", data);
        }
    }

    @RabbitListener(queues = "label.data.test.queue", ackMode = "NONE")
    public void listener6(Message message) {
        log.debug("队列{}监听消费：{}", message.getMessageProperties().getConsumerQueue(), message);
        String bodyStr = new String(message.getBody(), StandardCharsets.UTF_8);
        log.debug("String：{}", bodyStr);
        JSONObject jsonObject = JSON.parseObject(bodyStr);
        log.debug("jsonObject：{}", JSON.toJSONString(jsonObject, true));
        JSONArray array = jsonObject.getJSONArray("data");
        log.debug("array：{}", JSON.toJSONString(array, true));
    }

    public static void main(String[] args) {
        byte[] body = new byte[]{123, 34, 99, 104, 97, 110, 110, 101, 108, 95, 105, 100, 34, 58, 34, 49, 57, 52, 34, 44, 34, 99, 104, 97, 110, 110, 101, 108, 95, 110, 97, 109, 101, 34, 58, 34, -25, -81, -82, -25, -112, -125, -27, -114, -126, -24, -67, -90, -24, -66, -122, -24, -65, -99, -27, -127, -100, 50, 92, 116, 34, 44, 34, 99, 104, 97, 110, 110, 101, 108, 95, 115, 116, 121, 108, 101, 34, 58, 34, 71, 85, 78, 34, 44, 34, 100, 101, 118, 105, 99, 101, 95, 110, 97, 109, 101, 34, 58, 34, -25, -81, -82, -25, -112, -125, -27, -114, -126, -24, -67, -90, -24, -66, -122, -24, -65, -99, -27, -127, -100, 50, 92, 116, 34, 44, 34, 100, 101, 118, 105, 99, 101, 95, 116, 121, 112, 101, 34, 58, 34, 72, 73, 75, 95, 73, 80, 67, 34, 44, 34, 104, 101, 105, 103, 104, 116, 34, 58, 48, 46, 48, 44, 34, 104, 111, 114, 105, 122, 111, 110, 116, 97, 108, 34, 58, 53, 57, 46, 48, 44, 34, 105, 110, 99, 108, 105, 110, 97, 116, 105, 111, 110, 95, 97, 110, 103, 108, 101, 34, 58, 48, 46, 48, 44, 34, 108, 97, 116, 105, 116, 117, 100, 101, 34, 58, 48, 46, 48, 44, 34, 108, 111, 110, 103, 105, 116, 117, 100, 101, 34, 58, 48, 46, 48, 44, 34, 110, 111, 114, 116, 104, 95, 97, 110, 103, 108, 101, 34, 58, 48, 46, 48, 44, 34, 112, 97, 110, 34, 58, 50, 48, 46, 54, 48, 48, 48, 48, 48, 51, 56, 49, 52, 54, 57, 55, 50, 55, 44, 34, 116, 105, 108, 116, 34, 58, 49, 48, 46, 53, 44, 34, 116, 105, 109, 101, 34, 58, 49, 54, 54, 51, 55, 50, 51, 57, 56, 48, 57, 57, 51, 44, 34, 118, 101, 114, 116, 105, 99, 97, 108, 34, 58, 51, 52, 46, 49, 53, 48, 48, 48, 49, 53, 50, 53, 56, 55, 56, 57, 48, 54, 44, 34, 122, 111, 111, 109, 34, 58, 49, 46, 48, 125, 10};
        String bodyStr = new String(body);
        System.out.println(bodyStr);
    }

    // 申请订阅标签   创建队列监听路由获取坐标数据
    // 取消订阅标签   删除队列监听
}