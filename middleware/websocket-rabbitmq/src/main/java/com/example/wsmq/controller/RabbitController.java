package com.example.wsmq.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("rabbit")
public class RabbitController {
    @Resource
    private AmqpTemplate amqpTemplate;

    private static final String EXCHANGE = "Meta.LabelService.RealTime";
    private static final String ROUTING_KEY = "SubscribeLabel.RealTime";

    public static final String ACTION_TYPE_SUB = "SubscribeLabel.RealTime";
    public static final String ACTION_TYPE_UNSUB = "UnSubscribeLabel.RealTime";
    public static final String ACTION_TYPE_EXIT = "ClientExit.RealTime";

    @GetMapping("send/topic")
    public String mqSendTopic(@RequestParam(defaultValue = "user-1") String id,
                              @RequestParam(defaultValue = "605aa32b423c4258b8c8b630d2350547") String data,
                              @RequestParam(defaultValue = "SubscribeLabel.RealTime") String code) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("data", data);
        jsonObject.put("code", code);
        amqpTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, jsonObject);
        return jsonObject.toJSONString();
    }

    @ApiOperation("申请订阅")
    @GetMapping("subscribe/label")
    public Object sub(@RequestParam String userId, @RequestParam String channelId) {
        Map<String, String> paramMap = sendMessage(userId, channelId, ACTION_TYPE_SUB);
        return paramMap;
    }

    @ApiOperation("取消订阅")
    @GetMapping("unSubscribe/label")
    public Object unSub(@RequestParam String userId, @RequestParam String channelId) {
        Map<String, String> paramMap = sendMessage(userId, channelId, ACTION_TYPE_UNSUB);
        return paramMap;
    }

    @ApiOperation("退出客户端")
    @GetMapping("/client/exit")
    public Object exit(@RequestParam String userId, @RequestParam String channelId) {
        Map<String, String> paramMap = sendMessage(userId, channelId, ACTION_TYPE_EXIT);
        return paramMap;
    }

    private Map<String, String> sendMessage(@RequestParam String userId, @RequestParam String channelId, String actionTypeExit) {
        Map<String, String> param = new HashMap<>(4);
        param.put("id", userId);
        param.put("data", channelId);
        param.put("code", actionTypeExit);
        amqpTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, param);
        log.info("消息发送{}",JSON.toJSONString(param, true));
        return param;
    }
}
