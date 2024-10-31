package com.example.temp.controller;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/test")
public class TestController {
    @SneakyThrows
    @GetMapping("/get")
    public Person testGet(@Valid Person person) {
        log.debug("testGet  输出： {}", person);
        return person;
    }

    @SneakyThrows
    @PostMapping("/post")
    public Person testPost(@RequestBody @Valid Person person) {
        log.debug("testPost  输出：{}", person);
        System.out.println(Integer.lowestOneBit(3628800));

        return person;
    }

    @GetMapping("/config")
    public Map<String, Object> testConfig(@RequestParam @NotNull Integer name) {
        Map<String, Object> result = new HashMap<>();
        result.put("date", new Date());
        result.put("localDate", LocalDate.now());
        result.put("localTime", LocalTime.now());
        result.put("localDateTime", LocalDateTime.now());
        result.put("long.class", BigInteger.valueOf(9007199254740997583L));
        result.put("null", null);
        log.debug("config  输出：{}", result);
        return result;
    }

    @PostMapping("/callback")
    public void testCallback(String eventId, String status, @RequestBody JSONObject msg) {
        log.info("callback  输出：{}", msg.toJSONString(JSONWriter.Feature.PrettyFormat));
        log.info("callback  输出：{}", JSONObject.toJSONString(msg, JSONWriter.Feature.PrettyFormat));
        log.info("callback  输出：{} {}", eventId, status);
    }

    @Data
    public static class Person {
        @NotBlank
        private String name;

        @ApiModelProperty("localDate")
        private LocalDate localDate;

        @ApiModelProperty("localTime")
        private LocalTime localTime;

        @ApiModelProperty("localDateTime")
        private LocalDateTime localDateTime;

        @ApiModelProperty("date")
        private Date date;

        @NotNull
        private Integer age;

        @NotEmpty
        private List<String> child;

        @Valid
        private Student student;
    }

    @Data
    public static class Student {
        @NotBlank
        private String stuName;
    }
}