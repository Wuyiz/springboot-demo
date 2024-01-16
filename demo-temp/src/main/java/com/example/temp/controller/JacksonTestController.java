package com.example.temp.controller;

import com.example.common.domain.ResponseResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/jackson")
@Validated
public class JacksonTestController {
    @ApiOperation("get")
    @GetMapping("/get")
    public ResponseResult<String> get(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date,
                                      @RequestParam(required = false) LocalDateTime localDateTime,
                                      @RequestParam(required = false) Integer testInt
    ) {
        log.info("get");
        return ResponseResult.success("get");
    }

    @ApiOperation("getObj")
    @GetMapping("/getObj")
    public Object getObj(Params params) {
        log.info("getObj");
        return "getObj";
    }

    @ApiOperation("post")
    @PostMapping("/post")
    public Object post(@RequestParam(required = false) Date date,
                       @RequestParam(required = false) LocalDateTime localDateTime,
                       @RequestParam(required = false) Integer testInt
    ) {
        log.info("post");
        return "post";
    }

    @ApiOperation("postObj")
    @PostMapping("/postObj")
    public Object postObj(@RequestBody Body body) {
        log.info("postObj");
        return "postObj";
    }

    @Data
    public static class Params {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date date;

        private LocalDateTime localDateTime;

        private Integer testInt;
    }

    @Data
    public static class Body {
        private Date date;

        private LocalDateTime localDateTime;

        private Integer testInt;
    }
}