package com.example.temp.controller;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.example.common.result.Result;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @SneakyThrows
    @GetMapping("/1")
    public Result<?> testGet(@RequestBody() Integer user, @RequestParam Integer id) {
        log.debug("1  输出：{}   {}", user, id);
        return Result.success();
    }

    @SneakyThrows
    @GetMapping("/2")
    public Result<?> testPost(Integer person) {
        log.debug("2  输出：{}", person);
        System.out.println(Integer.lowestOneBit(3628800));
        return Result.success();
    }

    @GetMapping("/config")
    public Result<?> testConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("datetime", new Date());
        result.put("long", BigInteger.valueOf(143));
        result.put("null", null);
        log.debug("config  输出：{}", result);
        return Result.success(result);
    }

    @PostMapping("/callback")
    public Result<?> testCallback(String eventId, String status, @RequestBody JSONObject msg) {
        log.info("callback  输出：{}", msg.toJSONString(JSONWriter.Feature.PrettyFormat));
        log.info("callback  输出：{}", JSONObject.toJSONString(msg, JSONWriter.Feature.PrettyFormat));
        log.info("callback  输出：{}{}", eventId, status);
        return Result.success();
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