package com.example.temp.controller;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.example.common.result.ResponseResult;
import com.example.temp.service.TestService;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test")
@Validated
public class TestController {
    @Resource
    private TestService testService;

    @GetMapping("/async")
    public ResponseResult<?> testAsync() {
        testService.asyncMethod();
        return ResponseResult.success();
    }

    @SneakyThrows
    @GetMapping("/get")
    public ResponseResult<?> testGet(Person person) {
        log.debug("testGet  输出： {}", person);
        return ResponseResult.success();
    }

    @SneakyThrows
    @PostMapping("/post")
    public ResponseResult<?> testPost(Person person) {
        log.debug("testPost  输出：{}", person);
        System.out.println(Integer.lowestOneBit(3628800));

        return ResponseResult.success();
    }


    @GetMapping("/config")
    public ResponseResult<?> testConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("datetime", new Date());
        result.put("long", BigInteger.valueOf(143));
        result.put("null", null);
        log.debug("config  输出：{}", result);
        return ResponseResult.success(result);
    }

    @PostMapping("/callback")
    public ResponseResult<?> testCallback(String eventId, String status, @RequestBody JSONObject msg) {
        log.info("callback  输出：{}", msg.toJSONString(JSONWriter.Feature.PrettyFormat));
        log.info("callback  输出：{}", JSONObject.toJSONString(msg, JSONWriter.Feature.PrettyFormat));
        log.info("callback  输出：{}{}", eventId, status);
        return ResponseResult.success();
    }

    @Data
    static class Person {
        @NotBlank
        private String name;

        @NotNull
        private Integer age;

        @NotEmpty
        private List<String> child;
    }
}